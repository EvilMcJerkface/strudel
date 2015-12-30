package com.nec.strudel.instrument;


public interface ProfilerService {

	void register(Object managedObject);

	void forceRegister(Object managedObject);

	<T> T getOrRegister(String name, T obj);

	<T> T getOrRegister(T obj);

	<T> Instrumented<T> createProfiler(Class<T> cls, Object stat);
	<T> Instrumented<T> createProfiler(Class<T> cls);
	OperationStat createOperationStat(int windowSize, long stepMs);

	BinaryEventStat createBinaryEventStat(int windowSize, long stepMs);

}