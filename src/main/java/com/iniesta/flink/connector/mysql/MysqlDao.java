package com.iniesta.flink.connector.mysql;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iniesta.flink.connector.mqtt.MqttMessage;

public class MysqlDao implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(MysqlDao.class);

	private String url;
	private String user;
	private String pass;
	private String driver;
	private transient Connection connection;
	
	public MysqlDao(Properties props) {
		url = props.getProperty("jdbc.url");
		user = props.getProperty("jdbc.user");
		pass = props.getProperty("jdbc.pass");
		driver = props.getProperty("jdbc.driver");
	}

	public void open() {
		if (connection == null) {
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url, user, pass);
				createTable();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (connection != null) {
			logger.debug("Destroying connection to: {}:{}", url, user);
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connection = null;
	}

	public void process(MqttMessage msg) {
		insertData(msg);
	}

	private static String escapeSQL(String s) {
		return s.replaceAll("'", "\\'");
	}
	
	private void insertData(MqttMessage msg) {
        try {
            String insertQuery = "insert into messages(topic, payload, ts) values (?,?,now())";
			PreparedStatement insertStmnt = connection.prepareStatement(insertQuery );
            insertStmnt.setString(1, escapeSQL(msg.getTopic()));
            insertStmnt.setString(2, escapeSQL(msg.getPayload()));
            insertStmnt.execute();            
        } catch (SQLException e) {
            logger.error("Error during the updating of the event", e);
        }
    }

	private void createTable() {
		String createTable = "create table if not exists messages(id int not null auto_increment primary key, topic varchar(255), payload varchar(10000),ts datetime)";
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(createTable);
		}catch(SQLException e) {
			logger.error("Error during the creation of the table", e);
		}
	}

	public List<String> testQuery() throws SQLException {
		List<String> output = new ArrayList<>();
		PreparedStatement ps = connection.prepareStatement("select id, a, b from data");
		ResultSet rs = ps.executeQuery();
		while(rs!=null && rs.next()) {
			String res = String.format("(%s,%s,%s)", rs.getInt(1), rs.getInt(2), rs.getInt(3));
			output.add(res);
		}
		return output;
	}
}

