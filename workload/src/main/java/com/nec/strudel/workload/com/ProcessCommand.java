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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.ConfigException;

public class ProcessCommand implements Command {
    private final String[] args;
    private final Properties prop;
    private final String input;

    public ProcessCommand(
            String[] args, String input, Properties env) {
        this.args = args;
        this.input = input;
        this.prop = env;
    }

    public static ProcessCommand create(ProcessCommandConfig spec) {
        return new ProcessCommand(combine(spec.getCommand(), spec.getArgs()),
                spec.getInput(), spec.getEnv());
    }

    private static String[] combine(String cmd, String[] as) {
        String[] args = new String[as.length + 1];
        args[0] = cmd;
        for (int i = 0; i < as.length; i++) {
            args[i + 1] = as[i];
        }
        return args;

    }

    @Override
    public CommandResult run(CommandContext ctxt) throws InterruptedException {
        Logger logger = ctxt.logger();
        logger.info("system command: " + Arrays.toString(args));
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        Map<String, String> env = pb.environment();
        for (String key : prop.stringPropertyNames()) {
            env.put(key, prop.getProperty(key));
        }
        try {
            Process proc = pb.start();
            logger.info("system process started");
            pipeInput(proc.getOutputStream());
            int exitValue = proc.waitFor();
            logger.info("system process done: exit=" + exitValue);
            if (exitValue != 0) {
                String msg = "system command failed with exit="
                        + exitValue;
                String out = procOutToIndentString(
                        proc.getInputStream());
                return CommandResult.error(msg, out);
            } else {
                String out = procOutToIndentString(
                        proc.getInputStream());
                return CommandResult.success(out);
            }
        } catch (IOException ex) {
            throw new ConfigException("system command failed", ex);
        }
    }

    void pipeInput(OutputStream out) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(out));
        writer.write(input);
        writer.flush();
        writer.close();
    }

    public String[] getArgs() {
        return args;
    }

    protected String procOutToIndentString(InputStream instr)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(instr));
        String line = br.readLine();
        while (line != null) {
            sb.append('\t').append(line).append('\n');
            line = br.readLine();
        }
        return sb.toString();
    }

}