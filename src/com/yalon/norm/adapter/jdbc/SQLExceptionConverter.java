package com.yalon.norm.adapter.jdbc;

import java.sql.SQLException;

import com.yalon.norm.NormSQLException;

public interface SQLExceptionConverter {
	NormSQLException convert(SQLException e);
}
