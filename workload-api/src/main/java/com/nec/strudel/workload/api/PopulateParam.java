package com.nec.strudel.workload.api;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

public class PopulateParam {
	private final Map<String, Object> values;
	private final int id;
	private final Random rand;
	public PopulateParam(int id, Map<String, Object> values, Random rand) {
		this.id = id;
		this.values = values;
		this.rand = rand;
	}
	public int getId() {
		return id;
	}

	@Nullable
	public Object get(String name) {
		return values.get(name);
	}
	@Nullable
	public Object get(Enum<?> name) {
		return get(name.name());
	}

	public int getInt(Enum<?> name) {
		return getInt(name.name());
	}
	public int getInt(String name) {
		Object v = get(name);
		if (v != null) {
			if (v instanceof Integer) {
				return (Integer) v;
			} else {
				return Integer.parseInt(v.toString());
			}
		} else {
			return 0;
		}
	}

	public double getDouble(Enum<?> name) {
		return getDouble(name.name());
	}
	public double getDouble(String name) {
		Object v = get(name);
		if (v != null) {
			if (v instanceof Double) {
				return (Double) v;
			} else {
				return Double.parseDouble(v.toString());
			}
		} else {
			return 0;
		}
	}


	public Random getRandom() {
		return rand;
	}
}
