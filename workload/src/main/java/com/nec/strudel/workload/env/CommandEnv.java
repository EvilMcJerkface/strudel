package com.nec.strudel.workload.env;

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CommandResult;
import com.nec.strudel.workload.com.Commands;

public class CommandEnv implements Environment {
    private static final Logger LOGGER = Logger.getLogger(CommandEnv.class);

    @Override
    public void start(ExecConfig conf) {
    	runCommand("start", conf);
    }

    @Override
    public void stop(ExecConfig conf) {
    	runCommand("stop", conf);
    }
    @Override
    public void startSuite(ExecConfig conf) {
    	runCommand("startSuite", conf);
    }
    @Override
    public void stopSuite(ExecConfig conf) {
    	runCommand("stopSuite", conf);
    }

    protected void runCommand(String name, ExecConfig conf) {
    	Command com = conf.toCommand();
    	if (com != null) {
        	runCommand(name, com);
        } else {
            LOGGER.debug("no command for " + name);
    	}
    }

    protected void runCommand(String name, Command com) {
        try {
        	LOGGER.info("starting command for " + name);
        	CommandResult res = com.run(Commands.createContext(LOGGER));
        	if (res.isSuccessful()) {
        		String log = res.getLog();
                LOGGER.info(name + " command done"
                	+ (log.isEmpty() ? "" : "\n" + log));
        	} else {
        		String msg = res.getMsg();
            	LOGGER.error(msg + "\n" + res.getLog());
            	throw new WorkloadException(msg);
        	}
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        	LOGGER.error("interrupted", e);
        	throw new WorkloadException(
        			name + " interrupted", e);
		}
    }


}
