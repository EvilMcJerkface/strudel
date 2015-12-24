package com.nec.strudel.session.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateParam;
import com.nec.strudel.util.RandomSelector;

public class ParamBuilderImpl implements ParamBuilder {
	private final State state;
	private final Param param;
	public ParamBuilderImpl(State state, Param param) {
		this.state = state;
		this.param = param;
	}
	public ParamBuilder use(StateParam p) {
		param.put(p, state.get(p));
		return this;
	}
	public ParamBuilder use(LocalParam p, StateParam src) {
		param.put(p, state.get(src));
		return this;
	}
	public boolean defined(StateParam p) {
		return state.get(p) != null;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T get(StateParam p) {
		return (T) state.get(p);
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(StateParam p) {
		Object o = state.get(p);
		if (o == null) {
			return new ArrayList<T>();
		} else if (o instanceof List) {
			return (List<T>) o;
		} else if (o instanceof Collection) {
			return new ArrayList<T>((Collection<T>) o);
		} else {
			List<T> list = new ArrayList<T>();
			list.add((T) o);
			return list;
		}
	}
	public ParamBuilder set(LocalParam p, Object value) {
		param.put(p, value);
		return this;
	}

	public ParamBuilder randomInt(LocalParam name,
			StateParam minName, StateParam maxName) {
		param.put(name, getRandomInt(minName, maxName));
		return this;
	}
	/**
	 * Defines a random integer ID
	 * @param name the parameter to be defined
	 * @param minName the minimum ID
	 * @param sizeName the size of (consecutive) ID numbers.
	 * @param excludeName the ID to be excluded
	 * @return this
	 */
	public ParamBuilder randomIntId(LocalParam name,
			StateParam minName, StateParam sizeName,
			StateParam excludeName) {
		param.put(name, getRandomIntId(minName, sizeName, excludeName));
		return this;
	}
	public int getRandomIntId(StateParam minName, StateParam sizeName,
			StateParam excludeName) {
		int exclude = getInt(excludeName);
		int min = getInt(minName);
		int size = getInt(sizeName);
		int max = min + size;
		if (size == 1 && min == exclude) {
			throw new RuntimeException(
				"invalid randomInt:[" + min
				+ "," + max + ") excluding " + exclude);
		} else if (size <= 0) {
			throw new RuntimeException(
					"invalid randomInt:[" + min
					+ "," + max + ")");
		}
		int value = 0;
		RandomSelector<Integer> ints = RandomSelector.create(
				min, max);
		do {
			value = ints.next(state.getRandom());
		} while (exclude == value);
		return value;
	}
	/**
	 * Defines a random integer ID
	 * @param name the parameter to be defined
	 * @param minName the minimum ID
	 * @param sizeName the size of (consecutive) ID numbers.
	 * @param excludeName the ID to be excluded
	 * @return this
	 */
	public ParamBuilder randomIntId(LocalParam name,
			StateParam minName, StateParam sizeName) {
		param.put(name, getRandomIntId(minName, sizeName));
		return this;
	}
	public int getRandomIntId(StateParam minName, StateParam sizeName) {
		int min = getInt(minName);
		int size = getInt(sizeName);
		int max = min + size;
		if (size <= 0) {
			throw new RuntimeException(
					"invalid randomInt:[" + min
					+ "," + max + ")");
		}
		return getRandomInt(min, max);
	}
	public Set<Integer> getRandomIntIdSet(StateParam countName,
			StateParam minName, StateParam sizeName) {
		int count = getInt(countName);
		int min = getInt(minName);
		int size = getInt(sizeName);
		int max = min + size;
		if (size < count) {
			throw new RuntimeException(
					"invalid randomIntSet("
					+ count + "):[" + min
					+ "," + max + ")");
		}
		Set<Integer> set = new HashSet<Integer>();
		if (count > size / 2) {
			for (int i = min; i < max; i++) {
				set.add(i);
			}
			while (set.size() > count) {
				set.remove(getRandomInt(min, max));
			}
		} else {
			while (set.size() < count) {
				set.add(getRandomInt(min, max));
			}
		}
		return set;
	}

	public ParamBuilder randomDouble(LocalParam name,
			StateParam minName, StateParam maxName) {
		param.put(name, getRandomDouble(minName, maxName));
		return this;
	}

	public ParamBuilder randomAlphaString(LocalParam p, int length) {
		param.put(p, getRandomAlphaString(length));
		return this;
	}
	public ParamBuilder randomAlphaString(LocalParam p,
			StateParam lengthParam) {
		return randomAlphaString(p, getInt(lengthParam));
	}
	public int getInt(StateParam pname) {
		return state.getInt(pname);
	}
	public double getDouble(StateParam name) {
		return state.getDouble(name);
	}
	public int getRandomInt(StateParam minName, StateParam maxName) {
		return getRandomInt(getInt(minName), getInt(maxName));
	}
    public int getRandomInt(int min, int max) {
    	return RandomSelector.create(min, max).next(state.getRandom());
    }
    public double getRandomDouble(StateParam minName, StateParam maxName) {
    	return getRandomDouble(getDouble(minName), getDouble(maxName));
    }
    public double getRandomDouble(double min, double max) {
        return RandomSelector.createDouble(min, max).next(state.getRandom());
    }
    public double getRandomDouble() {
    	return state.getRandom().nextDouble();
    }
    public int getRandomInt(int max) {
    	return state.getRandom().nextInt(max);
    }
    public String getRandomAlphaString(int length) {
    	return RandomSelector.createAlphaString(length).next(state.getRandom());
    }

}
