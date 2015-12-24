package com.nec.strudel.session;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Result {
	private final boolean success;
	private final Map<String, Object> values;
	private final List<Warn> warns;
	private final String mode;
	public Result(boolean success,
			Map<String, Object> values, List<Warn> warns) {
		this(success, "", values, warns);
	}
	public Result(boolean success, String mode,
			Map<String, Object> values, List<Warn> warns) {
		this.success = success;
		this.mode = mode;
		this.values = Collections.unmodifiableMap(values);
		this.warns = Collections.unmodifiableList(warns);
	}
	public boolean isSuccess() {
		return success;
	}
	@SuppressWarnings("unchecked")
	public <T> T get(ParamName p) {
		return (T) values.get(p.name());
	}
	/**
	 * @return an empty string if no mode
	 * is defined.
	 */
	public String getMode() {
		return mode;
	}
	public boolean hasMode() {
		return !mode.isEmpty();
	}
	public boolean hasWarning() {
		return !warns.isEmpty();
	}
	public List<Warn> getWarnings() {
		return warns;
	}
	public static class Warn {
		private final String msg;
		public Warn(String msg) {
			this.msg = msg;
		}
		public String getMessage() {
			return msg;
		}
	}
}
