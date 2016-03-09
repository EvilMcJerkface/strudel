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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nec.congenio.ConfigValue;
import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.workload.util.TimeValue;

public class Workflow {
    public static final String PARALLEL = "parallel";
    public static final String SEQ = "seq";
    public static final String SLEEP = "sleep";
    public static final String INFO = "info";
    public static final String SYSTEM = "system";

    private static final Map<String, ActionBuilder> BUILDERS = new HashMap<String, ActionBuilder>();

    static {
        BUILDERS.put(SLEEP, new SleepAction());
        BUILDERS.put(INFO, new InfoAction());
        BUILDERS.put(SYSTEM, new SystemAction());
    }

    private final List<ConfigValue> seq;
    private final Map<String, ActionBuilder> builders = new HashMap<String, ActionBuilder>(
            BUILDERS);

    public Workflow(List<ConfigValue> seq) {
        this.seq = seq;
    }

    public Workflow(ConfigValue conf) {
        this.seq = conf.toValueList();
    }

    public void addBuilder(String name, ActionBuilder builder) {
        builders.put(name, builder);
    }

    public Command createCommand() {
        CommandBuilder builder = new CommandBuilder();
        buildSequence(seq, builder);
        return builder.build();
    }

    protected Command buildCommand(ConfigValue action) {
        CommandBuilder builder = new CommandBuilder();
        buildAction(action, builder);
        return builder.build();
    }

    protected void buildSequence(List<ConfigValue> seq,
            CommandBuilder builder) {
        for (ConfigValue v : seq) {
            buildAction(v, builder);
        }
    }

    protected void buildParallel(List<ConfigValue> par,
            CommandBuilder builder) {
        List<Command> commands = new ArrayList<Command>();
        for (ConfigValue a : par) {
            commands.add(buildCommand(a));
        }
        builder.parallel(
                commands.toArray(new Command[commands.size()]));
    }

    protected void buildAction(ConfigValue action,
            CommandBuilder builder) {
        String name = action.getName();
        if (PARALLEL.equals(name)) {
            buildParallel(action.toValueList(), builder);
        } else if (SEQ.equals(name)) {
            buildSequence(action.toValueList(),
                    builder);
        } else if (name != null) {
            ActionBuilder ab = builderOf(name);
            if (ab == null) {
                throw new ConfigException(
                        "unknown action: " + name);
            }
            ab.build(action, builder);
        } else {
            throw new ConfigException(
                    "action name missing");
        }
    }

    ActionBuilder builderOf(String tagName) {
        return builders.get(tagName);
    }

    static class SleepAction implements ActionBuilder {

        @Override
        public void build(ConfigValue action, CommandBuilder builder) {
            int seconds = action.intValue(0);
            builder.sleep(TimeValue.seconds(seconds));
        }

    }

    static class SystemAction implements ActionBuilder {

        @Override
        public void build(ConfigValue action,
                CommandBuilder builder) {
            Command com = CommandFactory.create(action);
            builder.command(com);
        }
    }

    static class InfoAction implements ActionBuilder {

        @Override
        public void build(ConfigValue action,
                CommandBuilder builder) {
            String msg = action.stringValue();
            if (msg != null) {
                builder.info(msg);
            } else {
                throw new ConfigException(
                        "missing message content in info");
            }
        }
    }

}
