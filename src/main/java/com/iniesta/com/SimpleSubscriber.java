package com.iniesta.com;

import java.io.FileInputStream;
import java.util.Properties;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.iniesta.flink.connector.mqtt.MqttMessage;

public class SimpleSubscriber {

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(args[0]));
		
		String hostName = props.getProperty("mqtt.host");
		int port = Integer.parseInt(props.getProperty("mqtt.port", "1883"));		
		String topic = props.getProperty("mqtt.topic");
		
		
		MQTT mqtt = new MQTT();
		mqtt.setHost(hostName, port);
		BlockingConnection blockingConnection = mqtt.blockingConnection();
		try {
		blockingConnection.connect();
		} catch(Exception ex) {
			System.out.println("[Source] Not connected");
			ex.printStackTrace();
		}
		System.out.println("[Source] Connected to mqtt broker");
		
		byte[] qoses = blockingConnection.subscribe(new Topic[] {new Topic(topic, QoS.AT_LEAST_ONCE)});
		System.out.println("[Source] Subscribe to " + topic);
		
		while(blockingConnection.isConnected()) {
			Message message = blockingConnection.receive();
			System.out.println("[Source] Receiving message, now process internal message");
			MqttMessage mmsg = new MqttMessage(message.getTopic(), new String(message.getPayload()));
			System.out.println("[Source]: " + mmsg);
			message.ack();
			System.out.println("[msg] "+ mmsg);
		}
		blockingConnection.disconnect();
	}
}
