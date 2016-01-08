/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.tkvs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


/**
 * Record is the value of a key-value pair and consists of
 * a vector of string values.
 * @author tatemura
 *
 */
public final class Record {
	private final String[] values;

	public static Record create(String... values) {
		return new Record(values);
	}
	public static Record create(Object... values) {
	    String[] vals = new String[values.length];
	    for (int i = 0; i < vals.length; i++) {
	        vals[i] = values[i].toString();
	    }
	    return new Record(vals);
	}


	private Record(String[] values) {
		this.values = values;
	}

	/**
	 * Gets an element of the record
	 * @param index the index of the element
	 * @return the String value of the element.
	 */
	public String get(int index) {
		return values[index];
	}
	public int getInt(int index) {
		return Integer.parseInt(get(index));
	}
	public long getLong(int index) {
		return Long.parseLong(get(index));
	}
	public double getDouble(int index) {
		return Double.parseDouble(get(index));
	}

	public <T> T get(int index, Class<T> type) {
		String val = get(index);
		return convert(val, type);
	}
	public Object[] toTuple(Class<?>[] types) {
		Object[] tuple = new Object[types.length];
		for(int i = 0; i < tuple.length; i++) {
			tuple[i] = get(i, types[i]);
		}
		return tuple;
	}
	@SuppressWarnings("unchecked")
	public static <T> T convert(String val, Class<T> type) {
		if (type.equals(String.class)) {
			return (T) val;
		} else if (type.equals(Integer.class)
				|| type.equals(int.class)) {
			return (T) Integer.valueOf(val);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return (T) Long.valueOf(val);
		} else if (type.equals(Double.class)
				|| type.equals(double.class)) {
			return (T) Double.valueOf(val);
		} else {
			throw new RuntimeException(
					"unknown type: " + type);
		}
	}
	public int size() {
		return values.length;
	}
	public Collection<String> values() {
		return Collections.unmodifiableCollection(
				Arrays.asList(values));
	}
	protected String[] getValues() {
		return values;
	}
	public Record append(String... additionals) {
		String[] newVals = Arrays.copyOf(
		        values, values.length + additionals.length);
		for (int i = 0; i < additionals.length; i++) {
			newVals[i + values.length] = additionals[i];
		}
		return new Record(newVals);
	}
	public Record removeAt(int idx) {
		String[] vals = new String[values.length - 1];
		for (int i = 0; i < idx; i++) {
			vals[i] = values[i];
		}
		for (int i = idx; i < vals.length; i++) {
			vals[i] = values[i + 1];
		}
		return new Record(vals);
	}
	@Override
	public int hashCode() {
		return Arrays.hashCode(values);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Record) {
			Record r = (Record) obj;
			return Arrays.equals(values, r.values);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("{");
		boolean beginning = true;
		for (String value : values) {
		    if (beginning) {
		        beginning = false;
		    } else {
		        buff.append(", ");
		    }
			buff.append(value);
		}
		return buff.append("}")
		        .toString();
	}
}

