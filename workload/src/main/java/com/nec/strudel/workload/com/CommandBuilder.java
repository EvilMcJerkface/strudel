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
