package com.yalon.norm.test;

import com.yalon.norm.adapter.jdbc.SqliteJDBCDatabase;
import com.yalon.norm.mapper.EntityMap;

public class Sample1 {
	public static void main(String[] args) throws Exception {
		SqliteJDBCDatabase db = new SqliteJDBCDatabase("unittest.db");
		EntityMap map = new EntityMap();
		Vehicle.DAO dao = new Vehicle.DAO(db, map);
		for (Vehicle v : dao.findAll()) {
			System.out.println("vehicle=" + v);
		}
	}
}