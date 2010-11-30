/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yalon.norm.utils;

import java.text.Collator;

public class DatabaseUtils {
	/**
	 * Appends an SQL string to the given StringBuilder, including the opening
	 * and closing single quotes. Any single quotes internal to sqlString will
	 * be escaped.
	 * 
	 * This method is deprecated because we want to encourage everyone to use
	 * the "?" binding form. However, when implementing a ContentProvider, one
	 * may want to add WHERE clauses that were not provided by the caller. Since
	 * "?" is a positional form, using it in this case could break the caller
	 * because the indexes would be shifted to accomodate the ContentProvider's
	 * internal bindings. In that case, it may be necessary to construct a WHERE
	 * clause manually. This method is useful for those cases.
	 * 
	 * @param sb
	 *            the StringBuilder that the SQL string will be appended to
	 * @param sqlString
	 *            the raw string to be appended, which may contain single quotes
	 */
	public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
		sb.append('\'');
		if (sqlString.indexOf('\'') != -1) {
			int length = sqlString.length();
			for (int i = 0; i < length; i++) {
				char c = sqlString.charAt(i);
				if (c == '\'') {
					sb.append('\'');
				}
				sb.append(c);
			}
		} else
			sb.append(sqlString);
		sb.append('\'');
	}

	/**
	 * SQL-escape a string.
	 */
	public static String sqlEscapeString(String value) {
		StringBuilder escaper = new StringBuilder();

		DatabaseUtils.appendEscapedSQLString(escaper, value);

		return escaper.toString();
	}

	/**
	 * Appends an Object to an SQL string with the proper escaping, etc.
	 */
	public static final void appendValueToSql(StringBuilder sql, Object value) {
		if (value == null) {
			sql.append("NULL");
		} else if (value instanceof Boolean) {
			Boolean bool = (Boolean) value;
			if (bool) {
				sql.append('1');
			} else {
				sql.append('0');
			}
		} else {
			appendEscapedSQLString(sql, value.toString());
		}
	}

	/**
	 * Concatenates two SQL WHERE clauses, handling empty or null values.
	 * 
	 * @hide
	 */
	public static String concatenateWhere(String a, String b) {
		if (a.isEmpty()) {
			return b;
		}
		if (b.isEmpty()) {
			return a;
		}

		return "(" + a + ") AND (" + b + ")";
	}

	/**
	 * return the collation key
	 * 
	 * @param name
	 * @return the collation key
	 */
	public static String getCollationKey(String name) {
		byte[] arr = getCollationKeyInBytes(name);
		try {
			return new String(arr, 0, getKeyLen(arr), "ISO8859_1");
		} catch (Exception ex) {
			return "";
		}
	}

	private static int getKeyLen(byte[] arr) {
		if (arr[arr.length - 1] != 0) {
			return arr.length;
		} else {
			// remove zero "termination"
			return arr.length - 1;
		}
	}

	private static byte[] getCollationKeyInBytes(String name) {
		if (mColl == null) {
			mColl = Collator.getInstance();
			mColl.setStrength(Collator.PRIMARY);
		}
		return mColl.getCollationKey(name).toByteArray();
	}

	private static Collator mColl = null;

}
