package com.nec.strudel.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;


@NotThreadSafe
public class Param implements Iterable<Map.Entry<String, Object>> {
	private final Map<String, Object> values =
		new HashMap<String, Object>();

	public Param() {
	}
	public Param(Map<String, Object> values) {
		this.values.putAll(values);
	}
	@Override
	public Iterator<Entry<String, Object>> iterator() {
		return Collections.unmodifiableSet(
				values.entrySet()).iterator();
	}
	@Nullable
	public String get(ParamName p) {
		Object value = values.get(p.name());
		if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}

	public void put(ParamName p, Object value) {
		values.put(p.name(), value);
	}
	/**
	 * Gets an object associated with the given name.
	 * <p>
	 * Do not modify this object.
	 * Notice that this object might be shared by
	 * multiple threads. Use of an immutable object is
	 * highly encouraged.
	 * @param p the parameter name of the object.
	 * @return null if there is no such object.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T getObject(ParamName p) {
		return (T) values.get(p.name());
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> getObjectList(ParamName p) {
		Object v = values.get(p.name());
		if (v == null) {
			return new ArrayList<T>();
		} else if (v instanceof List) {
			return (List<T>) v;
		} else if (v instanceof Collection) {
			Collection <T> c = (Collection<T>) v;
			return new ArrayList<T>(c);
		} else {
			T item = (T) v;
			List<T> list = new ArrayList<T>(1);
			list.add(item);
			return list;
		}
	}
	public int getInt(ParamName p) {
		String value = get(p);
		if (value != null) {
		    try {
	            return Integer.parseInt(value);
		    } catch (NumberFormatException e) {
		        throw new NumberFormatException(
		          "value of " + p.name() + " must be integer: "
		          + value);
		    }
		}
		return 0;
	}
	public long getLong(ParamName p) {
        String value = get(p);
        if (value != null) {
            try {
	            return Long.parseLong(value);
		    } catch (NumberFormatException e) {
		        throw new NumberFormatException(
		          "value of " + p.name() + " must be long: "
		          + value);
		    }
        }
        return 0;
	}
	public double getDouble(ParamName p) {
		String value = get(p);
		if (value != null) {
			try {
	            return Double.parseDouble(value);
		    } catch (NumberFormatException e) {
		        throw new NumberFormatException(
		          "value of " + p.name() + " must be double: "
		          + value);
		    }
		} else {
			return 0;
		}
	}

	public void clear() {
		values.clear();
	}

	@Override
	public String toString() {
		return values.toString();
	}

}
