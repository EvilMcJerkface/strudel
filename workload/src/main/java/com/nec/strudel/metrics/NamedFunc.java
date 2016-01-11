/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
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
package com.nec.strudel.metrics;

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