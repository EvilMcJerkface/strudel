package com.nec.strudel.workload.com;


public interface Command {
	CommandResult run(CommandContext ctxt) throws InterruptedException;
}