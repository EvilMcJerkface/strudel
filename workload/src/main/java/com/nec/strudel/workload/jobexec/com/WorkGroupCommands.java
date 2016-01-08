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
package com.nec.strudel.workload.jobexec.com;

import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;

import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CommandContext;
import com.nec.strudel.workload.com.CommandResult;
import com.nec.strudel.workload.jobexec.WorkExecContext;
import com.nec.strudel.workload.util.TimeUtil;
import com.nec.strudel.workload.worker.WorkGroup;

public final class WorkGroupCommands {

	public static Command command(String command, JsonObject arg) {
		return new WorkGroupCommand(command, arg);
	}
	public static Command start() {
		return new WorkGroupStart();
	}
	public static Command stop() {
		return new WorkGroupStop();
	}
	public static Command waitForState(String state) {
		return new WaitForState(state);
	}

	static class WorkGroupCommand implements Command {
		private final String command;
		private final JsonObject arg;
		WorkGroupCommand(String command, JsonObject arg) {
			this.command = command;
			this.arg = arg;
		}
	
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			WorkGroup wg = ((WorkExecContext) ctxt).workGroup();
			wg.operate(command, arg);
			return CommandResult.success();
		}
	}

	static class WorkGroupStart implements Command {
		public WorkGroupStart() {
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			WorkGroup wg = ((WorkExecContext) ctxt).workGroup();
			synchronized (wg) {
				wg.start();
			}
			return CommandResult.success();
		}
	
	}

	static class WorkGroupStop implements Command {
		public WorkGroupStop() {
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			WorkGroup wg = ((WorkExecContext) ctxt).workGroup();
			synchronized (wg) {
				wg.stop();
			}
			return CommandResult.success();
		}
	}

	public static class WaitForState implements Command {
		private static final int EXTRA_WAIT = 1000;
		private static final long WARNABLE_DELAY =
				TimeUnit.SECONDS.toMillis(30);
		private final String state;
		public WaitForState(String state) {
			this.state = state;
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			waitState(ctxt);
			return CommandResult.success();
		}
		boolean hasTodo(boolean[] todo) {
			for (boolean t : todo) {
				if (t) {
					return true;
				}
			}
			return false;
		}
		int count(boolean[] todo) {
			int count = 0;
			for (boolean t : todo) {
				if (t) {
					count++;
				}
			}
			return count;
		}
		protected void waitState(CommandContext ctxt)
				throws InterruptedException {
			WorkGroup wg = ((WorkExecContext) ctxt).workGroup();
			long expectedEndTime = System.currentTimeMillis();
			int total = wg.size();
			boolean[] todo = new boolean[total];
			for (int i = 0; i < todo.length; i++) {
				todo[i] = true;
			}
			while (hasTodo(todo)) {
				String[] states = wg.getStates(todo);
				for (int i = 0; i < total; i++) {
					if (todo[i] && done(states[i])) {
						todo[i] = false;
					}
				}
				int count = count(todo);
				if (count > 0) {
					long delay =
						System.currentTimeMillis()
							- expectedEndTime;
					if (delay > 0) {
						if (delay > WARNABLE_DELAY) {
							ctxt.logger().warn("delay: "
						+ TimeUtil.formatTimeMS(delay)
							+ ", complete: "
							+ (total - count)
							+ "/" + total);
						} else {
							ctxt.logger().debug("delay: "
						+ TimeUtil.formatTimeMS(delay));
						}
					}
					Thread.sleep(EXTRA_WAIT);
				}
			}
		}
		private boolean done(String state) {
			return this.state.equals(state);
		}
	}

	private WorkGroupCommands() {
	}

}
