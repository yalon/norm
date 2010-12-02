package com.yalon.norm.persist;

import com.yalon.norm.NormSQLException;
import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.annotations.Entity.Polyphormic;
import com.yalon.norm.sqlite.ddl.ConflictAlgorithm;

@Entity(polymorphic = Polyphormic.NO)
public class PersistentObject implements Persistable {
	@Column(dbToObjectOnly = true)
	private Long id;

	/* (non-Javadoc)
	 * @see com.yalon.norm.persist.Persistable1#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof PersistentObject)) {
			return false;
		}
		return this.getId() == ((PersistentObject) obj).getId();
	}

	/* (non-Javadoc)
	 * @see com.yalon.norm.persist.Persistable1#hasId()
	 */
	@Override
	public boolean hasId() {
		return id != null;
	}

	/* (non-Javadoc)
	 * @see com.yalon.norm.persist.Persistable1#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	public void save() {
		save(null);
	}

	public void save(ConflictAlgorithm conflictResolution) {
		if (hasId()) {
			PersistencyManager.update(this, conflictResolution);
		} else {
			this.id = PersistencyManager.insert(this, conflictResolution);
		}
	}

	public void destroy() {
		if (!hasId()) {
			// TODO: exceptions.
			throw new NormSQLException("object " + this + " does not have an ID.");
		}
		PersistencyManager.destroy(this);
	}
}