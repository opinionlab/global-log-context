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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple test fixture. Just run its main method.
 */
public class LogAppenderTestFixture {
  public static final int PORT = 5555;

  /**
   * Use this method to test the appender. Run this first, then run
   * GlobalContextSocketAppender::main
   *
   * @param args Program arguments. None are needed.
   * @throws java.lang.Exception if things go wrong
   */
  @SuppressWarnings("InfiniteLoopStatement")
  public static void main(String[] args) throws Exception {
    ServerSocket serverSocket = new ServerSocket(PORT);

    System.out.println("Starting listen loop.");
    while (true) {
      try {
        final Socket clientSocket = serverSocket.accept();
        System.out.println("Received client connection.");
        new Thread() {
          @Override
          public void run() {
            ObjectInputStream i = null;
            try {
              i = new ObjectInputStream(clientSocket.getInputStream());
              while (true) {
                Object received = i.readObject();
                System.out.println(ToStringBuilder
                    .reflectionToString(received,
                        ToStringStyle.SHORT_PREFIX_STYLE));
                Thread.sleep(1000);
              }
            } catch (EOFException e) {
              System.out.println("Client closed connection.");
            } catch (Throwable t) {
              t.printStackTrace();
            } finally {
              if (i != null) {
                try {
                  i.close();
                } catch (Throwable t) {
                  t.printStackTrace();
                }
              }
            }
          }
        }.start();
        Thread.sleep(1000);
      } catch (Throwable t) {
        t.printStackTrace();
      }
      System.out.println("Next...");
    }

  }
}
