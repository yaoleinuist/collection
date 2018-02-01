package com.lzhsite.hive;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

public class HiveJdbcClient {
	private static final String DRIVERNAME = "org.apache.hive.jdbc.HiveDriver";

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		try {
			Class.forName(DRIVERNAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Connection con = DriverManager.getConnection(//
				"jdbc:hive2://hadoop.senior02:10000/default",//
				"root",
				"123456.."//
			);
		Statement stmt = con.createStatement();
		String tableName = "emp";

		// select * query
		String sql = "select * from " + tableName;
		System.out.println("Running: " + sql);
		ResultSet res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
		}

	}
}