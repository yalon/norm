package com.yalon.norm.mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.DataRow;
import com.yalon.norm.NormException;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.utils.ReflectionUtils;

public class EntityMap {
	protected static final Logger LOG = LoggerFactory.getLogger(EntityMap.class);

	protected Map<Class<?>, EntityMapper> classToEntityMapper;

	public EntityMap() {
		this.classToEntityMapper = Collections
				.synchronizedMap(new HashMap<Class<?>, EntityMapper>());
	}

	public EntityMapper get(Class<?> clazz) {
		EntityMapper mapper = classToEntityMapper.get(clazz);
		if (mapper == null) {
			throw new NormException("class " + clazz + " was not registered");
		}
		return mapper;
	}

	public void putIfNotExists(Class<?> clazz) {
		synchronized (classToEntityMapper) {
			if (classToEntityMapper.containsKey(clazz)) {
				return;
			}

			// We need to map the parents first from top to bottom, and only
			// then the child.
			Stack<Class<?>> classes = new Stack<Class<?>>();
			Class<?> parent = clazz.getSuperclass();
			while (parent != Object.class) {
				classes.push(parent);
				parent = parent.getSuperclass();
			}

			EntityMapper parentMapper = null;
			while (!classes.isEmpty()) {
				Class<?> c = classes.pop();
				EntityMapper mapper = classToEntityMapper.get(c);
				if (mapper != null) {
					parentMapper = mapper;
				} else if (c.isAnnotationPresent(Entity.class)) {
					parentMapper = new EntityMapper(parentMapper, c);
					classToEntityMapper.put(c, parentMapper);
				}
			}

			if (parentMapper == null && !clazz.isAnnotationPresent(Entity.class)) {
				throw new NormSQLException("class " + clazz + " is not an entity");
			}

			EntityMapper mapper = new EntityMapper(parentMapper, clazz);
			classToEntityMapper.put(clazz, mapper);
			LOG.debug("new mapper class={} mapper={}", clazz, mapper);
		}
	}

	public EntityMapper get(String className) {
		return get(ReflectionUtils.classForName(className));
	}

	public void mapObjectToRow(Object obj, Map<String, Object> row) {
		EntityMapper mapper = get(obj.getClass());
		mapper.mapObjectToRow(obj, row);
	}

	public <T> T mapRowToNewObject(DataRow row, Class<?> objClass) {
		EntityMapper mapper = get(objClass);
		if (mapper.isPolymorphic()) {
			String className = mapper.getPolymorphicInstanceClassName(row);
			mapper = get(className);
		}

		return mapper.mapRowToNewObject(row);
	}
}