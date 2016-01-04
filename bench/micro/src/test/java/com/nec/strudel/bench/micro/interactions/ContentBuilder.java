package com.nec.strudel.bench.micro.interactions;

import com.nec.strudel.bench.test.EntityBuilder;
import com.nec.strudel.entity.EntityUtil;

public class ContentBuilder<E> implements EntityBuilder<E> {
	private String prefix;
	private String name;
	private Object key;

	public ContentBuilder(String name, Object key) {
		this.name = name;
		this.key = key;
		this.prefix = "test:";
	}
	public ContentBuilder<E> prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	@Override
	public void build(E entity, int idx) {
		EntityUtil.setProperty(entity, name, key);
		EntityUtil.setProperty(entity, "content", prefix + idx);
	}

}
