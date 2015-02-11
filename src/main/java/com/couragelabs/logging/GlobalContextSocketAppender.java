package com.couragelabs.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GlobalContextSocketAppender extends SocketAppender {
  public GlobalContextSocketAppender() {
    super();
  }

  public GlobalContextSocketAppender(String host, int port) {
    super(host, port);
  }

  private Map<String, String> globalContext;

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

  public Map<String, String> getGlobalContextAsMap() {
    if (globalContext == null) {
      globalContext = new HashMap<>();
      System.out.println("Warning: No globalContext specified.");
    }
    return globalContext;
  }

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
   */
  public static void main(String[] args) throws InterruptedException {
    Logger logger = Logger.getLogger(GlobalContextSocketAppender.class);
    logger.info("here you go");
    logger.debug("this is fun");

    System.out.println("Sleeping...");
    Thread.sleep(5000);
    System.out.println("Exiting...");
    System.exit(0);
  }
}
