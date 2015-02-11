package com.couragelabs.logging;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LogAppenderTestFixture {
  public static final int PORT = 5555;

  /**
   * Use this method to test the appender. Run this first, then run
   * GlobalContextSocketAppender::main
   */
  @SuppressWarnings("InfiniteLoopStatement")
  public static void main(String[] args) throws IOException,
      ClassNotFoundException, InterruptedException {
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
