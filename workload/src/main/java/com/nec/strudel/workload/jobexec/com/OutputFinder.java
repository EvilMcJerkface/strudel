package com.nec.strudel.workload.jobexec.com;

import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CompositeCommand;
import com.nec.strudel.workload.out.OutputSet;

public class OutputFinder {
	public static OutputSet output(Command com) {
		return new OutputFinder().findOutput(com);
	}
	public OutputSet findOutput(Command com) {
		OutputSet.Builder ob = OutputSet.builder();
		findOutput(com, ob);
		return ob.build();
	}
	private void findOutput(Command com, OutputSet.Builder ob) {
		if (com instanceof WorkloadCommand) {
			ob.add(((WorkloadCommand) com).outputs());
		} else if (com instanceof CompositeCommand) {
			CompositeCommand coms = (CompositeCommand) com;
			for (Command c : coms.commands()) {
				findOutput(c, ob);
			}
		}
	}
	
}