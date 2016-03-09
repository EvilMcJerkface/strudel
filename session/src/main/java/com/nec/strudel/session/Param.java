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
    private final Map<String, Object> values = new HashMap<String, Object>();

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
    public String get(ParamName paramName) {
        Object value = values.get(paramName.name());
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    public void put(ParamName paramName, Object value) {
        values.put(paramName.name(), value);
    }

    /**
     * Gets an object associated with the given name.
     * <p>
     * Do not modify this object. Notice that this object might be shared by
     * multiple threads. Use of an immutable object is highly encouraged.
     * 
     * @param paramName
     *            the parameter name of the object.
     * @return null if there is no such object.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getObject(ParamName paramName) {
        return (T) values.get(paramName.name());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getObjectList(ParamName paramName) {
        Object val = values.get(paramName.name());
        if (val == null) {
            return new ArrayList<T>();
        } else if (val instanceof List) {
            return (List<T>) val;
        } else if (val instanceof Collection) {
            Collection<T> collection = (Collection<T>) val;
            return new ArrayList<T>(collection);
        } else {
            T item = (T) val;
            List<T> list = new ArrayList<T>(1);
            list.add(item);
            return list;
        }
    }

    public int getInt(ParamName paramName) {
        String value = get(paramName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(
                        "value of " + paramName.name() + " must be integer: "
                                + value);
            }
        }
        return 0;
    }

    public long getLong(ParamName paramName) {
        String value = get(paramName);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(
                        "value of " + paramName.name() + " must be long: "
                                + value);
            }
        }
        return 0;
    }

    public double getDouble(ParamName paramName) {
        String value = get(paramName);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(
                        "value of " + paramName.name() + " must be double: "
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
