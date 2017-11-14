# Logjack - move logs without doing the heavy lifting.

Logjack reads log files and moves log lines to configured sender. A directory scanner constantly keeps track of the active log files and starts a log file reader when it finds a new log file in the directory. Log file reader sends log lines to the configured sender. 

Application configuration is in config.yml. 

To build the project: ./gradlew build 

To run: java -jar build/libs/logjack-1.0-SNAPSHOT.jar

Senders

http: Sends log lines to HTTP post URL with basic authentication if username and password is provided. Retries after specified time if HTTP post call fails.
