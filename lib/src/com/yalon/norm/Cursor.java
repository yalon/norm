package com.yalon.norm;

/**
 * This interface provides random read-write access to the result set returned
 * by a database query.
 */
public interface Cursor extends DataRow {
	/**
	 * Returns the numbers of rows in the cursor.
	 * 
	 * @return the number of rows in the cursor.
	 */
	int getCount();

	/**
	 * Returns the current position of the cursor in the row set. The value is
	 * zero-based. When the row set is first returned the cursor will be at
	 * positon -1, which is before the first row. After the last row is returned
	 * another call to next() will leave the cursor past the last entry, at a
	 * position of count().
	 * 
	 * @return the current cursor position.
	 */
	int getPosition();

	/**
	 * Move the cursor by a relative amount, forward or backward, from the
	 * current position. Positive offsets move forwards, negative offsets move
	 * backwards. If the final position is outside of the bounds of the result
	 * set then the resultant position will be pinned to -1 or count() depending
	 * on whether the value is off the front or end of the set, respectively.
	 * 
	 * <p>
	 * This method will return true if the requested destination was reachable,
	 * otherwise, it returns false. For example, if the cursor is at currently
	 * on the second entry in the result set and move(-5) is called, the
	 * position will be pinned at -1, and false will be returned.
	 * 
	 * @param offset
	 *            the offset to be applied from the current position.
	 * @return whether the requested move fully succeeded.
	 */
	boolean move(int offset);

	/**
	 * Move the cursor to an absolute position. The valid range of values is -1
	 * &lt;= position &lt;= count.
	 * 
	 * <p>
	 * This method will return true if the request destination was reachable,
	 * otherwise, it returns false.
	 * 
	 * @param position
	 *            the zero-based position to move to.
	 * @return whether the requested move fully succeeded.
	 */
	boolean moveToPosition(int position);

	/**
	 * Move the cursor to the first row.
	 * 
	 * <p>
	 * This method will return false if the cursor is empty.
	 * 
	 * @return whether the move succeeded.
	 */
	boolean moveToFirst();

	/**
	 * Move the cursor to the last row.
	 * 
	 * <p>
	 * This method will return false if the cursor is empty.
	 * 
	 * @return whether the move succeeded.
	 */
	boolean moveToLast();

	/**
	 * Move the cursor to the next row.
	 * 
	 * <p>
	 * This method will return false if the cursor is already past the last
	 * entry in the result set.
	 * 
	 * @return whether the move succeeded.
	 */
	boolean moveToNext();

	/**
	 * Move the cursor to the previous row.
	 * 
	 * <p>
	 * This method will return false if the cursor is already before the first
	 * entry in the result set.
	 * 
	 * @return whether the move succeeded.
	 */
	boolean moveToPrevious();

	/**
	 * Returns whether the cursor is pointing to the first row.
	 * 
	 * @return whether the cursor is pointing at the first entry.
	 */
	boolean isFirst();

	/**
	 * Returns whether the cursor is pointing to the last row.
	 * 
	 * @return whether the cursor is pointing at the last entry.
	 */
	boolean isLast();

	/**
	 * Returns whether the cursor is pointing to the position before the first
	 * row.
	 * 
	 * @return whether the cursor is before the first result.
	 */
	boolean isBeforeFirst();

	/**
	 * Returns whether the cursor is pointing to the position after the last
	 * row.
	 * 
	 * @return whether the cursor is after the last result.
	 */
	boolean isAfterLast();

	/**
	 * Deactivates the Cursor, making all calls on it fail until
	 * {@link #requery} is called. Inactive Cursors use fewer resources than
	 * active Cursors. Calling {@link #requery} will make the cursor active
	 * again.
	 */
	void deactivate();

	/**
	 * Performs the query that created the cursor again, refreshing its
	 * contents. This may be done at any time, including after a call to
	 * {@link #deactivate}.
	 * 
	 * @return true if the requery succeeded, false if not, in which case the
	 *         cursor becomes invalid.
	 */
	boolean requery();

	/**
	 * Closes the Cursor, releasing all of its resources and making it
	 * completely invalid. Unlike {@link #deactivate()} a call to
	 * {@link #requery()} will not make the Cursor valid again.
	 */
	void close();

	/**
	 * return true if the cursor is closed
	 * 
	 * @return true if the cursor is closed.
	 */
	boolean isClosed();
	
	/**
	 * return the platform's underlying cursor implementation (ResultSet for JDBC, android.Cursor for Android)
	 * 
	 * @return underlying platform's cursor implementation
	 */
	Object getPlatformCursor();
}