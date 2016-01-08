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
