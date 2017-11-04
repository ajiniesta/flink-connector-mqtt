package com.iniesta.flink.connector.mqtt;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class SocketTextStreamWordCount2Mqtt {

	//
	// Program
	//

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.err.println("USAGE:\nSocketTextStreamWordCount <hostname> <port>");
			return;
		}

		String hostName = args[0];
		Integer port = Integer.parseInt(args[1]);

		// set up the execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// get input data
		DataStream<String> text = env.socketTextStream(hostName, port);

		DataStream<Tuple2<String, Integer>> counts =
				// split up the lines in pairs (2-tuples) containing: (word,1)
				text.flatMap(new LineSplitter())
						// group by the tuple field "0" and sum up tuple field "1"
						.keyBy(0).sum(1);

		// counts.print();
		counts.addSink(new MqttSink<>("cydonia", "/samples"));

		// execute program
		env.execute("Java WordCount from SocketTextStream Example");
	}

	//
	// User Functions
	//

	/**
	 * Implements the string tokenizer that splits sentences into words as a
	 * user-defined FlatMapFunction. The function takes a line (String) and splits
	 * it into multiple pairs in the form of "(word,1)" (Tuple2<String, Integer>).
	 */
	public static final class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

		@Override
		public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
			// normalize and split the line
			String[] tokens = value.toLowerCase().split("\\W+");

			// emit the pairs
			for (String token : tokens) {
				if (token.length() > 0) {
					out.collect(new Tuple2<String, Integer>(token, 1));
				}
			}
		}
	}
}
