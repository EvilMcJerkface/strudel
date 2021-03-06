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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ParallelCommand implements CompositeCommand {
    private final Command[] coms;

    public ParallelCommand(Command... coms) {
        this.coms = coms;
    }

    @Override
    public CommandResult run(CommandContext ctxt)
            throws InterruptedException {
        List<Callable<CommandResult>> calls = new ArrayList<Callable<CommandResult>>();
        for (Command c : coms) {
            calls.add(new CommandCall(c, ctxt));
        }

        List<Future<CommandResult>> results = ctxt.call(calls);
        CommandResult commandResult = CommandResult.success();
        for (Future<CommandResult> res : results) {
            try {
                CommandResult cr = res.get();
                if (!cr.isSuccessful()) {
                    ctxt.logger().error(cr.getMsg());
                    commandResult = cr;
                }
            } catch (ExecutionException ex) {
                ctxt.logger().error("execution failed", ex);
                return CommandResult.error(
                        "parallel execution failed",
                        ex.getMessage());
            }
        }
        return commandResult;
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