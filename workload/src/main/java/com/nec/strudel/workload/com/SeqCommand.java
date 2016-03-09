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