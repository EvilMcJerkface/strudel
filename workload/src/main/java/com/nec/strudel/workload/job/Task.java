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
package com.nec.strudel.workload.job;

import com.nec.congenio.ConfigValue;
import com.nec.strudel.exceptions.ConfigException;

/**
 * Task is one unit of execution on the cluster.
 * Tasks are executed sequentially (within a task,
 * multiple threads on multiple servers may run in
 * parallel).
 * <p>
 * Currently there are the following types
 * of tasks:
 * <ul>
 * <li> Populate (Populate): a task that populates data in
 * the database.
 * <li> Workload (WorkloadTask): a task that runs a workload.
 * </ul>
 * @author tatemura
 *
 */
public abstract class Task {

    public static Task create(ConfigValue taskConf) {
        String name = taskConf.getName();
        if (PopulateTask.TAG_NAME.equals(name)) {
            return taskConf.toObject(PopulateTask.class);
        } else if (WorkloadTask.TAG_NAME.equals(name)) {
            return taskConf.toObject(WorkloadTask.class);
        } else {
            throw new ConfigException(
                   "unknown task: " + name);
        }

    }
    public abstract String description();
}
