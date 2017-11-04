package com.iniesta.flink.connector.mysql;

import java.util.Properties;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import com.iniesta.flink.connector.mqtt.MqttMessage;

public class MysqlSink extends RichSinkFunction<MqttMessage> {

	private MysqlDao mysqlDao;

	public MysqlSink(Properties props) {
		mysqlDao = new MysqlDao(props);
	}

	@Override
	public void invoke(MqttMessage msg) throws Exception {
		mysqlDao.process(msg);
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);
		mysqlDao.open();
	}

	@Override
	public void close() throws Exception {
		super.close();
		mysqlDao.close();
	}

}
