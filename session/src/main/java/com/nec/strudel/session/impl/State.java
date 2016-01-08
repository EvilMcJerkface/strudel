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
package com.nec.strudel.session.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.session.StateParam;

/**
 * A class that encapsulates the current state of
 * a session. The state includes (1) a set of
 * key-value pairs (key is specified with StateParam),
 * (1) a result mode (a String that categorizes the result
 * of the previous interaction.
 */
@NotThreadSafe
public class State implements Iterable<Map.Entry<String, Object>> {
	private final Map<String, Object> values;
	private final Random rand;
	private String mode = "";

	public static State newState(Map<String, Object> initialValues,
	        Random parent) {
		return new State(
				new HashMap<String, Object>(initialValues),
				new Random(parent.nextLong()));
	}

	State(Map<String, Object> values, Random rand) {
		this.values = values;
		this.rand = rand;
	}
	public State put(StateParam p, Object value) {
		values.put(p.name(), value);
		return this;
	}

	@Nullable
	public Object get(StateParam p) {
		return values.get(p.name());
	}
	public int getInt(StateParam name) {
		Object v = get(name);
		if (v instanceof Integer) {
			return (Integer) v;
		} else if (v != null) {
			return Integer.parseInt(v.toString());
		} else {
			return 0;
		}
	}
	public double getDouble(StateParam name) {
        Object v = get(name);
        if (v instanceof Double) {
            return (Double) v;
        } else if (v instanceof Integer) {
            return (Integer) v;
        } else if (v != null) {
            return Double.parseDouble(v.toString());
        } else {
            return 0;
        }
	}
	public long getLong(StateParam name) {
		Object v = get(name);
        if (v instanceof Long) {
            return (Long) v;
        } else if (v instanceof Integer) {
            return (Integer) v;
        } else if (v != null) {
            return Long.parseLong(v.toString());
        } else {
            return 0;
        }
	}

	@Override
	public Iterator<Entry<String, Object>> iterator() {
		return values.entrySet().iterator();
	}

	public boolean defines(StateParam name) {
		return values.containsKey(name.name());
	}
	public Random getRandom() {
		return rand;
	}

	/**
	 * Gets the mode of the previous result.
	 * @return an empty string if the mode
	 * is not defined
	 */
	public String getResultMode() {
		return mode;
	}
	public void setResultMode(String mode) {
		this.mode = mode;
	}

}
