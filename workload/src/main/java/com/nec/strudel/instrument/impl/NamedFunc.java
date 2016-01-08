package com.nec.strudel.instrument.impl;

import java.util.Map;

import com.nec.strudel.json.func.Func;

public class NamedFunc implements Map.Entry<String, Func> {
	private final Func func;
	private final String name;
	public NamedFunc(String name, Func func) {
		this.func = func;
		this.name = name;
	}
	@Override
	public String getKey() {
		return name;
	}
	@Override
	public Func getValue() {
		return func;
	}
	@Override
	public Func setValue(Func value) {
		throw new UnsupportedOperationException();
	}
	public String toString() {
		return name + ":" + func;
	}
}