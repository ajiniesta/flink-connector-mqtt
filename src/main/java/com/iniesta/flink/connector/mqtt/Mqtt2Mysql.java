package com.iniesta.flink.connector.mqtt;

import java.io.FileInputStream;
import java.util.Properties;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import com.iniesta.flink.connector.mysql.MysqlSink;


public class Mqtt2Mysql {

	//
	//	Program
	//

	public static void main(String[] args) throws Exception {

		ParameterTool pt = ParameterTool.fromArgs(args);
		
		String properties = pt.getRequired("conf");
		Properties props = new Properties();
		props.load(new FileInputStream(properties));
		
		String hostName = props.getProperty("mqtt.host");
		int port = Integer.parseInt(props.getProperty("mqtt.port", "1883"));		
		String topicIn = props.getProperty("mqtt.topic");
		System.out.println("Properties.... " + props.toString());

		// set up the execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment
				.getExecutionEnvironment();

		// get input data
		DataStream<MqttMessage> text = env.addSource(new MqttSource(hostName, port, topicIn));

//		DataStream<Tuple2<String, Integer>> counts =
		// split up the lines in pairs (2-tuples) containing: (word,1)
//		text.flatMap(new LineSplitter())
		// group by the tuple field "0" and sum up tuple field "1"
//				.keyBy(0)
//				.sum(1);

//		text.print();
//		counts.addSink(new MqttSink<>(hostName, topicOut));
		text.addSink(new MysqlSink(props));
		// execute program		
		int parallelism = Integer.parseInt(props.getProperty("flink.parallelism", "2"));
		env.setParallelism(parallelism);
		System.out.println("About to execute....");
		env.execute("Mqtt 2 Mysql");
	}

	//
	// 	User Functions
	//

	/**
	 * Implements the string tokenizer that splits sentences into words as a user-defined
	 * FlatMapFunction. The function takes a line (String) and splits it into
	 * multiple pairs in the form of "(word,1)" (Tuple2<String, Integer>).
	 */
	@SuppressWarnings("serial")
	public static final class LineSplitter implements FlatMapFunction<MqttMessage, Tuple2<String, Integer>> {

		@Override
		public void flatMap(MqttMessage value, Collector<Tuple2<String, Integer>> out) {
			// normalize and split the line
			String[] tokens = value.getPayload().toLowerCase().split("\\W+");

			// emit the pairs
			for (String token : tokens) {
				if (token.length() > 0) {
					out.collect(new Tuple2<String, Integer>(token, 1));
				}
			}
		}
	}	
}
