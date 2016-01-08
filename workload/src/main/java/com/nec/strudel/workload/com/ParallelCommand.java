package com.nec.strudel.workload.com;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ParallelCommand implements CompositeCommand {
	private final Command[] coms;
	public ParallelCommand(Command...coms) {
		this.coms = coms;
	}
	@Override
	public CommandResult run(CommandContext ctxt)
			throws InterruptedException {
		List<Callable<CommandResult>> calls =
			new ArrayList<Callable<CommandResult>>();
		for (Command c : coms) {
			calls.add(new CommandCall(c, ctxt));
		}

		List<Future<CommandResult>> results =
				ctxt.call(calls);
		CommandResult r = CommandResult.success();
		for (Future<CommandResult> res : results) {
			try {
				CommandResult cr = res.get();
				if (!cr.isSuccessful()) {
					ctxt.logger().error(cr.getMsg());
					r = cr;
				}
			} catch (ExecutionException e) {
				ctxt.logger().error("execution failed", e);
				return CommandResult.error(
					"parallel execution failed",
					e.getMessage());
			}
		}
		return r;
	}
	@Override
	public Command[] commands() {
		return coms;
	}
	public static class CommandCall implements Callable<CommandResult> {
		private final Command com;
		private final CommandContext ctxt;
		public CommandCall(Command com, CommandContext ctxt) {
			this.com = com;
			this.ctxt = ctxt;
		}
		@Override
		public CommandResult call() throws Exception {
			return com.run(ctxt);
		}
	}

}