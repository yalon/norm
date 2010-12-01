package com.yalon.norm.utils;

import java.util.Iterator;

public class StringUtils {
	public final static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public final static <T> void join(T[] array, String delim, StringBuilder builder) {
		join(array, delim, "", builder);
	}

	public final static <T> void join(T[] array, String delim, String initialDelim,
			StringBuilder builder) {
		String sep = initialDelim;
		for (int i = 0; i < array.length; ++i) {
			builder.append(sep);
			builder.append(array[i].toString());
			sep = delim;
		}
	}

	public final static <T> void join(Iterator<T> iter, String delim, StringBuilder builder) {
		join(iter, delim, "", builder);
	}

	public final static <T> void join(Iterator<T> iter, String delim, String initialDelim,
			StringBuilder builder) {
		String sep = initialDelim;
		while (iter.hasNext()) {
			builder.append(sep);
			builder.append(iter.next().toString());
			sep = delim;
		}
	}
}