package com.yalon.norm.utils;

import java.util.Iterator;

public class StringUtils {
	public final static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	
	public final static String repeat(String value, String delim, int count) {
		StringBuilder builder = new StringBuilder();
		repeat(value, delim, count, builder);
		return builder.toString();
	}
	
	public final static void repeat(String value, String delim, int count, StringBuilder builder) {
		String sep = "";
		for (int i = 0; i < count; ++i) {
			builder.append(sep);
			builder.append(value);
			sep = delim;
		}
	}

	public final static <T> String join(T[] array, String delim) {
		return join(array, delim, "");
	}

	public final static <T> String join(T[] array, String delim, String initialDelim) {
		StringBuilder builder = new StringBuilder();
		join(array, delim, initialDelim, builder);
		return builder.toString();
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