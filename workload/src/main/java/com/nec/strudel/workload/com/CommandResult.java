package com.nec.strudel.workload.com;

public class CommandResult {
	public static CommandResult success() {
		return new CommandResult(true, "", "");
	}
	public static CommandResult success(String log) {
		return new CommandResult(true, "", log);
	}
	public static CommandResult error(String msg, String log) {
		return new CommandResult(false, msg, log);
	}
	private final boolean success;
	private final String msg;
	private final String log;

	public CommandResult(boolean success,
			String msg, String log) {
		this.success = success;
		this.msg = msg;
		this.log = log;
	}
	public boolean isSuccessful() {
		return success;
	}
	public String getMsg() {
		return msg;
	}
	public String getLog() {
		return log;
	}
}