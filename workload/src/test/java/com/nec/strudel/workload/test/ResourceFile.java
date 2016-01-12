package com.nec.strudel.workload.test;

public interface ResourceFile<T> {

	String file();
	Class<T> resourceClass();
}
