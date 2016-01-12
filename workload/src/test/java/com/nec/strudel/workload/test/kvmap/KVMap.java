package com.nec.strudel.workload.test.kvmap;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.nec.strudel.target.FactoryClass;

@FactoryClass(KVMapCreator.class)
public class KVMap {
	private final ConcurrentHashMap<String, AtomicInteger> map =
		new ConcurrentHashMap<String, AtomicInteger>();
	private final String name;
	public KVMap(String name) {
		this.name = name;
	}
	public int get(String key) {
		AtomicInteger ai = map.get(key);
		if (ai != null) {
			return ai.get();
		} else {
			return 0;
		}
	}
	public void put(String key, int value) {
		map.put(key, new AtomicInteger(value));
	}
	public int add(String key, int value) {
		AtomicInteger ai = map.get(key);
		if (ai == null) {
			ai = new AtomicInteger();
			AtomicInteger old = map.putIfAbsent(key, ai);
			if (old != null) {
				ai = old;
			}
		}
		return ai.addAndGet(value);
	}
	public int size() {
	    return map.size();
	}
	public Set<String> keySet() {
	    return Collections.unmodifiableSet(map.keySet());
	}
	public boolean hasValue(String key) {
	    return map.containsKey(key);
	}

	public String getName() {
		return name;
	}
}
