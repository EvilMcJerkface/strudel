package com.nec.strudel.bench.test;

public interface EntityBuilder<E> {
	void build(E entity, int idx);
}