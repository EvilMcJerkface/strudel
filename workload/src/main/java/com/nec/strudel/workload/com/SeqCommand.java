package com.nec.strudel.workload.com;

public class SeqCommand implements CompositeCommand {
	private final Command[] coms;
	public SeqCommand(Command... coms) {
		this.coms = coms;
	}
	@Override
	public CommandResult run(CommandContext ctxt)
			throws InterruptedException {
		for (Command c : coms) {
			CommandResult res = c.run(ctxt);
			if (!res.isSuccessful()) {
				return res;
			}
		}
		return CommandResult.success();
	}
	@Override
	public Command[] commands() {
		return coms;
	}
}