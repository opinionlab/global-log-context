/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.couragelabs.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The GlobalContextSocketAppender only exists to ensure a global context is
 * provided to remote servers, regardless of the thread a log message may
 * have come from. This works around the inherent limitations in the
 * Mapped Diagnostic Context in Log4J.
 * <p>
 * Simply configure this appender with a GlobalContext which can be simple
 * JSON. The following formats are accepted:
 * </p>
 * <code>
 * # Generates a field in the MDC called "global" with the value "hello"<br>
 * log4j.appender.socket.GlobalContext="hello"<br><br>
 *
 * # Generates two fields in the MDC called "global0" and "global1", <br>
 * # each with values "hello" and "world," respectively.<br>
 * log4j.appender.socket.GlobalContext=["hello", "world"]<br><br>
 *
 * # Generates two fields in the MDS called "thing" and "bits", <br>
 * # each with values "stuff" and "junk," respectively.<br>
 * log4j.appender.socket.GlobalContext={"thing":"stuff", "bits":"junk"}
 * </code>
 */
public class GlobalContextSocketAppender extends SocketAppender {
  private Map<String, String> globalContext;

  /**
   * Update the given event with the global context then send it to the
   * superclass for sending to the remote server.
   * @param event Event to append.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void append(LoggingEvent event) {
    Map<String, String> contextMap = getGlobalContextAsMap();

    for (String key : contextMap.keySet()) {
      String value = contextMap.get(key);
      event.setProperty(key, value);
    }
    super.append(event);
  }

  /**
   * Retrieve a clean map of global context to append to each logging event.
   * @return A map of global context key/value pairs.
   */
  public Map<String, String> getGlobalContextAsMap() {
    if (globalContext == null) {
      globalContext = new HashMap<>();
      System.out.println("Warning: No globalContext specified.");
    }
    return globalContext;
  }

  /**
   * If you call setGlobalContext with "test" you will receive
   * {"global":"test"} out of this method. Overly clever? I think not! ;-)
   *
   * @return The fully processed global context property, serialized as JSON.
   */
  public String getGlobalContext() {
    if (globalContext != null) {
      JSONObject o = new JSONObject();
      o.putAll(globalContext);
      String globalContextStr = o.toJSONString();
      System.out.println(globalContextStr);
      return globalContextStr;
    }
    return null;
  }

  /**
   * @param globalContext Global context to set.
   * @throws ParseException If parsing the global context fails.
   */
  public void setGlobalContext(String globalContext) throws ParseException {
    if (globalContext != null) {
      this.globalContext = new HashMap<>();
      JSONParser parser = new JSONParser();
      Object parsed = parser.parse(globalContext);
      if (parsed instanceof String) {
        this.globalContext.put("global", (String) parsed);
      } else if (parsed instanceof JSONArray) {
        Iterator iterator = ((JSONArray) parsed).iterator();
        int i = 0;
        while (iterator.hasNext()) {
          this.globalContext.put("global[" + i + "]",
              String.valueOf(iterator.next()));
          i++;
        }
      } else if (parsed instanceof JSONObject) {
        JSONObject parsedObject = (JSONObject) parsed;
        for (Object key : parsedObject.keySet()) {
          this.globalContext.put(String.valueOf(key),
              String.valueOf(parsedObject.get(key)));
        }
      } else {
        System.err.println("Unable to handle context type: " + parsed.getClass());
      }
    }
  }

  /**
   * Use this method to test the log appender. First, run the VerySimpleLogger.
   *
   * @param args Program arguments. None are needed.
   * @throws java.lang.Exception if things go wrong
   */
  public static void main(String[] args) throws Exception {
    Logger logger = Logger.getLogger(GlobalContextSocketAppender.class);
    logger.info("here you go");
    logger.debug("this is fun");

    System.out.println("Sleeping...");
    Thread.sleep(5000);
    System.out.println("Exiting...");
    System.exit(0);
  }
}
