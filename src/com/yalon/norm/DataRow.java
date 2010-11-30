package com.yalon.norm;

public interface DataRow {

	/**
	 * Return total number of columns
	 * 
	 * @return number of columns
	 */
	int getColumnCount();

	/**
	 * Returns the zero-based index for the given column name, or -1 if the
	 * column doesn't exist. If you expect the column to exist use
	 * {@link #getColumnIndexOrThrow(String)} instead, which will make the error
	 * more clear.
	 * 
	 * @param columnName
	 *            the name of the target column.
	 * @return the zero-based column index for the given column name, or -1 if
	 *         the column name does not exist.
	 * @see #getColumnIndexOrThrow(String)
	 */
	int getColumnIndex(String columnName);

	/**
	 * Returns the zero-based index for the given column name, or throws
	 * {@link IllegalArgumentException} if the column doesn't exist. If you're
	 * not sure if a column will exist or not use
	 * {@link #getColumnIndex(String)} and check for -1, which is more efficient
	 * than catching the exceptions.
	 * 
	 * @param columnName
	 *            the name of the target column.
	 * @return the zero-based column index for the given column name
	 * @see #getColumnIndex(String)
	 * @throws IllegalArgumentException
	 *             if the column does not exist
	 */
	int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException;

	/**
	 * Returns the column name at the given zero-based column index.
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the column name for the given column index.
	 */
	String getColumnName(int columnIndex);

	/**
	 * Returns a string array holding the names of all of the columns in the
	 * result set in the order in which they were listed in the result.
	 * 
	 * @return the names of the columns returned in this query.
	 */
	String[] getColumnNames();

	/**
	 * Returns the value of the requested column as a byte array.
	 * 
	 * <p>
	 * If the native content of that column is not blob exception may throw
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the value of that column as a byte array.
	 */
	byte[] getBlob(int columnIndex);

	boolean getBoolean(int columnIndex);
	
	char getCharacter(int columnIndex);
	
	byte getByte(int columnIndex);

	/**
	 * Returns the value of the requested column as a double.
	 * 
	 * <p>
	 * If the native content of that column is not numeric the result will be
	 * the result of passing the column value to Double.valueOf(x).
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the value of that column as a double.
	 */
	double getDouble(int columnIndex);

	/**
	 * Returns the value of the requested column as a float.
	 * 
	 * <p>
	 * If the native content of that column is not numeric the result will be
	 * the result of passing the column value to Float.valueOf(x).
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the value of that column as a float.
	 */
	float getFloat(int columnIndex);

	/**
	 * Returns the value of the requested column as an int.
	 * 
	 * <p>
	 * If the native content of that column is not numeric the result will be
	 * the result of passing the column value to Integer.valueOf(x).
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the value of that column as an int.
	 */
	int getInt(int columnIndex);

	/**
	 * Returns the value of the requested column as a long.
	 * 
	 * <p>
	 * If the native content of that column is not numeric the result will be
	 * the result of passing the column value to Long.valueOf(x).
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the value of that column as a long.
	 */
	long getLong(int columnIndex);

	/**
	 * Retrieves the requested column text and stores it in the buffer provided.
	 * If the buffer size is not sufficient, a new char buffer will be allocated
	 * and assigned to CharArrayBuffer.data
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column. if the target
	 *            column is null, return buffer
	 * @param buffer
	 *            the buffer to copy the text into.
	 */
	// void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer);

	/**
	 * Returns the value of the requested column as a short.
	 * 
	 * <p>
	 * If the native content of that column is not numeric the result will be
	 * the result of passing the column value to Short.valueOf(x).
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the value of that column as a short.
	 */
	short getShort(int columnIndex);

	/**
	 * Returns the value of the requested column as a String.
	 * 
	 * <p>
	 * If the native content of that column is not text the result will be the
	 * result of passing the column value to String.valueOf(x).
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return the value of that column as a String.
	 */
	String getString(int columnIndex);

	/**
	 * Returns <code>true</code> if the value in the indicated column is null.
	 * 
	 * @param columnIndex
	 *            the zero-based index of the target column.
	 * @return whether the column value is null.
	 */
	boolean isNull(int columnIndex);
}
