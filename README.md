# Logjack - move logs without doing the heavy lifting.

Logjack reads log files and moves log lines to configured sender. A directory scanner constantly keeps track of the active log files and starts a log file reader when it finds a new log file in the directory. Log file reader sends log lines to the configured sender.

## Log message JSON

```
[{
	"message": "192.168.2.20 - - [28/Jul/2006:10:27:10 -0300] \"GET /cgi-bin/try/ HTTP/1.0\" 200 3395",
	"source": "apache.log",
	"timeUTC": 1511133128684,
	"hostIP": "192.168.1.199"
}, {
	"message": "127.0.0.1 - - [28/Jul/2006:10:22:04 -0300] \"GET / HTTP/1.0\" 200 2216",
	"source": "apache.log",
	"timeUTC": 1511133128684,
	"hostIP": "192.168.1.199"
}]
```

## Application configuration 

For an example please see config.yml in the root of the project. 

## Build the project 

./gradlew build 

## Run 

java -jar build/libs/logjack-1.0-SNAPSHOT.jar

## Senders

#### http (default)

Sends log lines to HTTP post URL with basic authentication if username and password is provided. Retries after specified time if HTTP post call fails.
