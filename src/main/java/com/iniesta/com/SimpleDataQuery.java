package com.iniesta.com;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import com.iniesta.flink.connector.mysql.MysqlDao;

public class SimpleDataQuery {

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(args[0]));
		
		MysqlDao dao = new MysqlDao(props);
		dao.open();
		
		List<String> res = dao.testQuery();
		System.out.println(res);
		
		dao.close();
		
	}
}
