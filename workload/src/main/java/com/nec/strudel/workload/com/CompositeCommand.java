package com.nec.strudel.workload.com;

public interface CompositeCommand extends Command {
	Command[] commands();
}
