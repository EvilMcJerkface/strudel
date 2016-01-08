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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultBuilder {
	private final Map<String, Object> values =
		new HashMap<String, Object>();
	private final List<Result.Warn> warns = new ArrayList<Result.Warn>();

	public ResultBuilder begin() {
		warns.clear();
		return this;
	}
	public ResultBuilder set(ParamName p, Object value) {
		values.put(p.name(), value);
		return this;
	}
	public ResultBuilder warn(String msg) {
		warns.add(new Result.Warn(msg));
		return this;
	}

	public Result failure(String mode) {
		return new Result(false, mode, values, warns);
	}
	public Result success() {
		return new Result(true, values, warns);
	}
	public Result success(String mode) {
		return new Result(true, mode, values, warns);
	}
}
