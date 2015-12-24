package com.nec.strudel.tkvs;

import java.util.List;

import com.nec.strudel.entity.info.BeanInfo;

public class EntityConstructor {
	private final BeanInfo info;
	private final Class<?>[] types;
	public EntityConstructor(Class<?> entityClass) {
		this(new BeanInfo(entityClass));
	}
	public EntityConstructor(BeanInfo info) {
		this.info = info;
		List<Class<?>> list = info.types();
		types = list.toArray(new Class<?>[list.size()]);
	}

	public Record toRecord(Object entity) {
		Object[] vals = info.toTuple(entity);
		return Record.create(vals);
	}

	public <T> T create(Record record) {
		@SuppressWarnings("unchecked")
		T entity = (T) info.create(record.toTuple(types));
		return entity;
	}

}
