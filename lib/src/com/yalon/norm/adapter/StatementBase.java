package com.yalon.norm.adapter;

import com.yalon.norm.Statement;

public abstract class StatementBase implements Statement {
	@Override
	public void bindByValueType(int index, Object value) {
		// TODO: reorder conditions according to likelihood
		if (value == null) {
			bindNull(index);
		} else if (value instanceof Boolean) {
			bindBoolean(index, (Boolean) value);
		} else if (value instanceof Byte) {
			bindByte(index, (Byte) value);
		} else if (value instanceof Short) {
			bindShort(index, (Short) value);
		} else if (value instanceof Integer) {
			bindInt(index, (Integer) value);
		} else if (value instanceof Long) {
			bindLong(index, (Long) value);
		} else if (value instanceof Float) {
			bindFloat(index, (Float) value);
		} else if (value instanceof Double) {
			bindDouble(index, (Double) value);
		} else if (value instanceof String) {
			bindString(index, (String) value);
		} else if (value instanceof byte[]) {
			bindBlob(index, (byte[]) value);
		}
	}
}