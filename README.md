# global-log-context

This is a very simple library for adding global context to an application's
SocketAppender. Very useful for distributed compute environments all
writing logs into a LogStash server.

Check out src/main/resources/log4j.properties for an example configuration.

## Developing

Use LogAppenderTestFixture to run a simple server that listens for log messages.

Run the main method in GlobalContextSocketAppender to send test log messages.

The log4j.properties file mentioned above declares the global context for all
the test messages.

Pull requests welcome!

## License

This project is licensed under the GNU GPL v3. See LICENSE for further
information.
