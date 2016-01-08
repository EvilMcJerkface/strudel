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

import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.workload.util.TimeValue;

public class CommandBuilder {
	private final List<Command> coms = new ArrayList<Command>();

	public CommandBuilder() {
	}
	public CommandBuilder command(Command com) {
		if (com instanceof SeqCommand) {
			for (Command c : ((SeqCommand) com).commands()) {
				command(c);
			}
		} else {
			coms.add(com);
		}
		return this;
	}
	public CommandBuilder info(String message) {
		coms.add(Commands.info(message));
		return this;
	}
	public CommandBuilder sleep(TimeValue time) {
		coms.add(Commands.sleep(time));
		return this;
	}

	public CommandBuilder parallel(Command...commands) {
		coms.add(Commands.parallel(commands));
		return this;
	}

	public Command build() {
		if (coms.size() == 1) {
			return coms.get(0);
		} else {
			return Commands.seq(coms);
		}
	}

}
