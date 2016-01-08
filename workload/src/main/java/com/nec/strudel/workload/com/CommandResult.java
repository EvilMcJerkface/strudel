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