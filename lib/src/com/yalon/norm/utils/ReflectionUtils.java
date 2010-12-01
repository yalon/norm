package com.yalon.norm.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yalon.norm.NormException;

public class ReflectionUtils {
	public static Class<?> classForName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new NormException(e);
		}
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			try {
				return clazz.newInstance();
			} catch (IllegalAccessException e) {
				Constructor<T> ctor = clazz.getDeclaredConstructor();
				boolean isAccessible = ctor.isAccessible();
				try {
					ctor.setAccessible(true);
					return ctor.newInstance();
				} finally {
					ctor.setAccessible(isAccessible);
				}
			}
		} catch (InstantiationException e) {
			throw new NormException(e);
		} catch (SecurityException e) {
			throw new NormException(e);
		} catch (NoSuchMethodException e) {
			throw new NormException(e);
		} catch (InvocationTargetException e) {
			throw new NormException(e);
		} catch (IllegalArgumentException e) {
			throw new NormException(e);
		} catch (IllegalAccessException e) {
			throw new NormException(e);
		}
	}

	public static Field getField(String fieldName, Object obj) {
		try {
			return obj.getClass().getField(fieldName);
		} catch (SecurityException e) {
			throw new NormException(e);
		} catch (NoSuchFieldException e) {
			throw new NormException(e);
		}
	}

	public static void setFieldValue(String fieldName, Object obj, Object value) {
		setFieldValue(getField(fieldName, obj), obj, value);
	}

	public static void setFieldValue(Field field, Object obj, Object value) {
		boolean isAccessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			throw new NormException(e);
		} catch (IllegalAccessException e) {
			throw new NormException(e);
		} finally {
			field.setAccessible(isAccessible);
		}
	}

	public static Object getFieldValue(String fieldName, Object obj) {
		return getFieldValue(getField(fieldName, obj), obj);
	}

	public static Object getFieldValue(Field field, Object obj) {
		boolean isAccessible = field.isAccessible();
		field.setAccessible(true);
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			throw new NormException(e);
		} catch (IllegalAccessException e) {
			throw new NormException(e);
		} finally {
			field.setAccessible(isAccessible);
		}
	}

	public static Method getMethod(String methodName, Object obj) {
		try {
			return obj.getClass().getMethod(methodName);
		} catch (SecurityException e) {
			throw new NormException(e);
		} catch (NoSuchMethodException e) {
			throw new NormException(e);
		}
	}

	public static Object callMethod(Method method, Object obj, Object... args) {
		boolean isAccessible = method.isAccessible();
		method.setAccessible(true);
		try {
			return method.invoke(obj, args);
		} catch (SecurityException e) {
			throw new NormException(e);
		} catch (IllegalArgumentException e) {
			throw new NormException(e);
		} catch (IllegalAccessException e) {
			throw new NormException(e);
		} catch (InvocationTargetException e) {
			throw new NormException(e);
		} finally {
			method.setAccessible(isAccessible);
		}
	}

	public static boolean isDatabasePrimitiveType(Class<?> clazz) {
		return clazz.isPrimitive() || clazz == Boolean.class || clazz == Short.class
				|| clazz == Integer.class || clazz == Long.class || clazz == Float.class
				|| clazz == Double.class || clazz == Character.class || clazz == Byte.class
				|| clazz == String.class || clazz == byte[].class;
	}
}
