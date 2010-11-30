package com.yalon.norm.test;

import java.util.Date;

import com.yalon.norm.Database;
import com.yalon.norm.EntityDAO;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.mapper.EntityMap;

@Entity(polymorphic = Entity.Polyphormic.YES)
public class Vehicle {
	enum Color {
		RED, GREEN, BLUE, BLACK
	}

	Color color;
	Date licenseDate;

	static class DAO extends EntityDAO<Vehicle> {
		public DAO(Database db, EntityMap map) {
			super(db, map, Vehicle.class);
		}
	};
}