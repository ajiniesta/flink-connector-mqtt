package com.iniesta.flink.connector.mqtt;

import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class BrokerPublisherManual {
	
	public static void main(String[] args) throws Exception {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				try {
					publish(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		ExecutorService pool = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 50; i++) {
			pool.submit(runnable);
		}
		
	}

	
	
	private static void publish(int num) throws URISyntaxException, Exception {
		MQTT mqtt = new MQTT();
		mqtt.setHost("cydonia", 1883);
		
		Random random = new Random();
		BlockingConnection blockingConnection = mqtt.blockingConnection();
		
		blockingConnection.connect();
		for (int i = 0; i < num; i++) {
			String payload = "Hello " + random.nextInt(10); 
			blockingConnection.publish("/samples/in", payload.getBytes(), QoS.AT_LEAST_ONCE, false);
		}
		
		blockingConnection.disconnect();
	}
}
