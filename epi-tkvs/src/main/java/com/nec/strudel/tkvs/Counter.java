package com.nec.strudel.tkvs;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Counter {
	public static final int INITIAL_VALUE = 1;
	private Key key;
	private int value;

	public Counter(Key key, Record record) {
		this.key = key;
		this.value = record.getInt(0);
	}
	public Counter(Key key) {
		this.key = key;
		this.value = INITIAL_VALUE;
	}
	public Key getKey() {
		return key;
	}
	public int nextValue() {
		int nextValue = value;
		value++;
		return nextValue;
	}
	public Record getRecord() {
		return Record.create(value);
	}

}
