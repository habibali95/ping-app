# ping-app

### how to use

- Running mvn clean install and Executing the java -jar command against the fat jar under target folder will run the
  app.
- This app will run on both Linux and Windows OS (it only tested on Linux)
### Configuration

- This app will read configurations by default from the pingApp.properties under resources however,
  you can pass your properties file path as a first argument for the java -jar command, furthermore you
  can declare the configuration as environment variable which will take precedence over the variable in
  your properties file.

example of configuration :  
hosts=google.com,facebook.com  
icmp_ping_delay=10000  
tcp_ping_delay=10000  
tcp_ping_timeout=1000  
trace_route_delay=10000  
report_url=http://localhost:8080

### Logging

- Basically everything is logged at warning level, it is changeable in log4j2.xml file, for the
  log path it is important to set environment LOG_PATH and the logs will be written under the desired
  folder, the logs will be printed on console as well.

- you can run the command and set the log path as follows
  LOG_PATH=/home/habib/Desktop/things/logs java -jar target/ping-app-1.0-SNAPSHOT.jar   