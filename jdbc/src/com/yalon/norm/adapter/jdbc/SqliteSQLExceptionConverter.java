package com.yalon.norm.adapter.jdbc;

import java.sql.SQLException;

import com.yalon.norm.NormSQLException;
import com.yalon.norm.NormUniqueConstraintException;

public class SqliteSQLExceptionConverter implements SQLExceptionConverter {
	public NormSQLException convert(SQLException e) {
		final String message = e.getMessage();
		NormSQLException result = null;

		if (message.equals("PRIMARY KEY must be unique") || message.contains(" not unique")) {
			result = new NormUniqueConstraintException(message);
		}

		if (result != null) {
			result.setStackTrace(e.getStackTrace());
		} else {
			result = new NormSQLException(e);
		}

		return result;
	}
}
