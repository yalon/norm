package com.yalon.norm.adapter.android;

import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;

import com.yalon.norm.NormSQLException;
import com.yalon.norm.NormUniqueConstraintException;

public class AndroidSQLExceptionConverter {
	NormSQLException convert(SQLException e) {
		NormSQLException result;
		if (e instanceof SQLiteConstraintException) {
			result = new NormUniqueConstraintException(e.getMessage());
			result.setStackTrace(e.getStackTrace());
		} else {
			result = new NormSQLException(e);
		}
		return result;
	}

}
