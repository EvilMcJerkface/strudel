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
