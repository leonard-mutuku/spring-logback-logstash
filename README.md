# spring-logback-logstash ELK Demo

Application demonstartes how to send application logs (spring java application) to logstash using logback encoder.

Dependencies required for the application to ship logs is the logback encoder below;

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.3</version>
</dependency>
```

Logback spring configuration(`logback-spring.xml`) is as below;

```xml
<configuration>
    
    <property scope="context" name="log.ext" value="log" />
    <springProperty scope="context" name="application.name" source="spring.application.name"/>
    <springProperty scope="context" name="log.dir" source="app.logs.location"/>
    <springProperty scope="context" name="logstash.enabled" source="elk.enabled"/>
    <springProperty scope="context" name="logstash.host" source="elk.logstash.server"/>
    <springProperty scope="context" name="logstash.port" source="elk.logstash.port"/>
    <contextName>${application.name}</contextName>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %contextName %F:%L - %msg%n
            </pattern>
        </encoder>
    </appender>
    
    <appender name="APP_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.dir}/app_%d{yyyy-MM-dd}.${log.ext}</fileNamePattern>
            <!-- keep 14 days' worth of history -->
            <maxHistory>14</maxHistory>
        </rollingPolicy>

        <encoder>
            <charset>UTF-8</charset>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %contextName %F:%L - %msg%n</pattern>
        </encoder>
    </appender>
    
    
    <logger name="com.logstash" level="DEBUG" />
    <logger name="org.springframework" level="INFO" />
    
    <root level="WARN">
        <appender-ref ref="STDOUT" />
    </root>
    
    <if condition='property("logstash.enabled").contains("true")'>
        <then>            
            <appender name="STASH_LOG" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
                <param name="Encoding" value="UTF-8"/>
                <destination>${logstash.host}:${logstash.port}</destination>
                <connectionTimeout>15 seconds</connectionTimeout>
                <keepAliveDuration>5 minutes</keepAliveDuration>
                <!-- encoder is required -->
                <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                    <customFields>{"service_name":"${application.name}", "tags":"SMS"}</customFields>
                </encoder>
            </appender>
            
            <logger name="APP_LOG" level="DEBUG" additivity="false">
                <appender-ref ref="APP_LOG"/>
                <appender-ref ref="STASH_LOG"/>
            </logger> 
        </then>
        <else>
            <logger name="APP_LOG" level="DEBUG" additivity="false">
                <appender-ref ref="APP_LOG"/>
            </logger> 
        </else> 
    </if>                      

</configuration>
```
`springProperty` values are derived from `application.yml` which can be overriden at runtime. The values are used to configure logging and also used turn on/off shipping of logs to logstash conditionally. If `logstash.enabled` value is set to false logging will only be for logback file (`/tmp/app_logs/*`). `STASH_LOG` is the appender configured for sending logs to logstash, where `<destination>` holds the address where the logstash instance is running.


Logstash configuration(`/etc/logstash/conf.d/*.conf`) is as below;

```conf
# Beats -> Logstash -> Elasticsearch pipeline.

input {

       	tcp {
              port => 5044
              codec => json_lines
        }
	udp {
             	port => 5044
        }

}

filter {
        if ([service_name] == "logstash-demo") {
                mutate {add_field => {"[@metadata][target_index]" => "logstash"}}
        }
	else {
              	mutate {add_field => {"[@metadata][target_index]" => "filebeat"}}
        }
}

output {
  elasticsearch {
    hosts => ["http://localhost:9200"]
    index => "%{[@metadata][target_index]}-%{+YYYY.MM.dd}"
    #user => "elastic"
    #password => "changeme"
  }
}
```

The above logstash configuration accepts tcp input connections on port 5044 (earlier configured destination for logstash encoder) and creates an output pipeline to elasticsearch on port 9200 with a logstash-%{+YYYY.MM.dd} index. The index can be accessed on kibana on port 5601 where it can be used to create an index(`logstash-*`) for visualization.

The config above also creates a filter that creates the index based on the service_name in the custom fields. This can be useful when need to create different index for different applications

![Screenshot from 2023-05-02 12-19-31](https://user-images.githubusercontent.com/6048594/235629579-390d958b-6911-47a9-ab21-dbfa11f39b00.png)


