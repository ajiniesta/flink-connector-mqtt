package com.iniesta.flink.connector.mqtt;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

public class MqttSink<T> extends RichSinkFunction<T> {

	private static final long serialVersionUID = 7883296716671354462L;

	private transient BlockingConnection blockingConnection;
	private String topic;
	private QoS qos;
	private boolean retain;

	private String host;

	private int port;

	public MqttSink(String host, String topic) {
		this(host, 1883, topic, QoS.AT_LEAST_ONCE, false);
	}

	
	public MqttSink(String host, int port, String topic)  {
		this(host, port, topic, QoS.AT_LEAST_ONCE, false);
	}
	
	public MqttSink(String host, int port, String topic, QoS qos, boolean retain) {
		this.host = host;
		this.port = port;		
		this.topic = topic;
		this.qos = qos;
		this.retain = retain;
	}
	
	@Override
	public void invoke(T event) throws Exception {
		byte[] payload = event.toString().getBytes();
		blockingConnection.publish(topic, payload, qos, retain);
	}

	@Override
	public void close() throws Exception {
		super.close();
		blockingConnection.disconnect();
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);
		MQTT mqtt = new MQTT();
		mqtt.setHost(host, port);
		blockingConnection = mqtt.blockingConnection();
		blockingConnection.connect();
	}

	
	
}
