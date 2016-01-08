package com.nec.strudel.workload.com;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.nec.strudel.workload.util.TimeValue;

public final class Commands {

	private Commands() {
		// not instantiated
	}

	public static Command parallel(Command...coms) {
		return new ParallelCommand(coms);
	}
	public static Command seq(Command... coms) {
		return new SeqCommand(coms);
	}
	public static Command seq(Collection<Command> coms) {
		return new SeqCommand(coms.toArray(new Command[coms.size()]));
	}
	public static Command sleep(long msec) {
		return new SleepCommand(msec);
	}
	public static Command sleep(TimeValue time) {
		return new SleepCommand(time.toMillis());
	}
	public static Command info(String message) {
		return new InfoCommand(message);
	}
	public static class SleepCommand implements Command {
		private final long time;
		SleepCommand(long msec) {
			this.time = msec;
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			if (time > 0) {
				Thread.sleep(time);
			}
			return CommandResult.success();
		}
	}
	static class InfoCommand implements Command {
		private final String message;
		public InfoCommand(String message) {
			this.message = message;
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			ctxt.logger().info(message);
			return CommandResult.success();
		}
	}
	public static CommandContext createContext(Logger logger) {
		return new DefaultContext(logger);
	}
	public static CommandContext createContext(Caller caller, Logger logger) {
		return new DelegatingContext(caller, logger);
	}

	static class DefaultContext implements CommandContext {
		private final Logger logger;
		public DefaultContext(Logger logger) {
			this.logger = logger;
		}
		@Override
		public Logger logger() {
			return logger;
		}
		@Override
		public <T> List<Future<T>> call(List<? extends Callable<T>> calls) {
			ExecutorService exec = Executors.newCachedThreadPool();
			List<Future<T>> results = new ArrayList<Future<T>>();
			for (Callable<T> c : calls) {
				results.add(exec.submit(c));
			}
			exec.shutdown();
			return results;
		}

	}
	static class DelegatingContext implements CommandContext {
		private final Caller caller;
		private final Logger logger;
		public DelegatingContext(Caller caller, Logger logger) {
			this.caller = caller;
			this.logger = logger;
		}
		@Override
		public <T> List<Future<T>> call(List<? extends Callable<T>> calls) {
			return caller.call(calls);
		}

		@Override
		public Logger logger() {
			return logger;
		}

	}

}
