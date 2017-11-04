package com.iniesta.flink.connector.mqtt;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class MqttSource extends RichSourceFunction<MqttMessage>{

	private String host;
	private int port;
	private String topic;
	private QoS qos;
	private boolean retain;

	public MqttSource(String host, String topic) {
		this(host, 1883, topic, QoS.AT_LEAST_ONCE, false);
	}
	
	public MqttSource(String host, int port, String topic) {
		this(host, port, topic, QoS.AT_LEAST_ONCE, false);
	}
	
	public MqttSource(String host, int port, String topic, QoS qos, boolean retain) {
		this.host = host;
		this.port = port;
		this.topic = topic;
		this.qos = qos;
		this.retain = retain;
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(SourceContext<MqttMessage> sourceContext) throws Exception {
		MQTT mqtt = new MQTT();
		mqtt.setHost(host, port);
		BlockingConnection blockingConnection = mqtt.blockingConnection();
		blockingConnection.connect();
		
		byte[] qoses = blockingConnection.subscribe(new Topic[] {new Topic(topic, qos)});
		
		while(blockingConnection.isConnected()) {
			Message message = blockingConnection.receive();
			MqttMessage mmsg = new MqttMessage(message.getTopic(), new String(message.getPayload()));
			message.ack();
			sourceContext.collect(mmsg);
		}
		blockingConnection.disconnect();
	}

}
