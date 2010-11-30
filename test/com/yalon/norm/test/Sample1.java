package com.yalon.norm.test;

import java.sql.Connection;
import java.sql.DriverManager;

import com.yalon.norm.adapter.jdbc.JDBCDatabase;
import com.yalon.norm.mapper.EntityMap;

public class Sample1 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		JDBCDatabase db = new JDBCDatabase(conn);
		EntityMap map = new EntityMap();
		Vehicle.DAO dao = new Vehicle.DAO(db, map);
		for (Vehicle v : dao.findAll()) {
			System.out.println("vehicle=" + v);
		}
	}
}