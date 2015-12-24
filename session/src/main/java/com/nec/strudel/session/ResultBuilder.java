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
