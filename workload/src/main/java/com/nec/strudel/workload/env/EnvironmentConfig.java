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

import javax.annotation.Nullable;

import com.nec.strudel.util.ClassUtil;

public class EnvironmentConfig {

    public static EnvironmentConfig empty() {
        return new EnvironmentConfig();
    }

    private String className = null;
    private String classPath = "";
    private ExecConfig start = new ExecConfig();
    private ExecConfig stop = new ExecConfig();

    private ExecConfig startSuite = new ExecConfig();

    private ExecConfig stopSuite = new ExecConfig();

    public EnvironmentConfig() {
    }

    public Environment create() {
        String className = getClassName();
        if (className != null) {
            return ClassUtil.create(className,
                    getClassPath());
        }
        return new CommandEnv();
    }

    @Nullable
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public ExecConfig getStart() {
        return start;
    }

    public void setStart(ExecConfig start) {
        this.start = start;
    }

    public ExecConfig getStop() {
        return stop;
    }

    public void setStop(ExecConfig stop) {
        this.stop = stop;
    }

    public ExecConfig getStartSuite() {
        return startSuite;
    }

    public void setStartSuite(ExecConfig startSuite) {
        this.startSuite = startSuite;
    }

    public ExecConfig getStopSuite() {
        return stopSuite;
    }

    public void setStopSuite(ExecConfig stopSuite) {
        this.stopSuite = stopSuite;
    }

    public void start(Environment env) {
        env.start(getStart());
    }

    public void stop(Environment env) {
        env.stop(getStop());
    }
}
