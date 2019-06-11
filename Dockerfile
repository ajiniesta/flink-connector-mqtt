FROM java:8

USER root

RUN mkdir /usr/mqtt2mysql

#RUN curl -Ls https://github.com/ajiniesta/flink-connector-mqtt/releases/download/0.1/flink-connector-mqtt-0.1.jar > /usr/mqtt2mysql/flink-con-mqtt.jar

COPY target/flink-connector-mqtt-*.jar /usr/mqtt2mysql/flink-con-mqtt.jar

CMD sleep 15 && java -cp /usr/mqtt2mysql/flink-con-mqtt.jar com.iniesta.flink.connector.mqtt.Mqtt2Mysql --conf /conf/conf.properties

 
