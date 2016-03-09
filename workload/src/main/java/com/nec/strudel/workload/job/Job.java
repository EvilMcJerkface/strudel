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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nec.congenio.ConfigDescription;
import com.nec.congenio.ConfigValue;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.workload.cluster.Cluster;
import com.nec.strudel.workload.env.EnvironmentConfig;
import com.nec.strudel.workload.out.OutputConfig;

/**
 * A Job is a unit of one independent experiment that consists of (1) starting
 * up the environment, (2) execution of a sequence of tasks, (3) reporting, and
 * (4) shut down (clean up) of the environment.
 * <ul>
 * <li>Environment (EnvironmentConfig) describes how the environment must be
 * started/shut down (e.g., deploying/starting key-value stores or RDBMSs).
 * <li>Cluster describes the cluster that runs the experiment workload.
 * <li>Database
 * <li>Task
 * <li>Reporter
 * </ul>
 * 
 * <pre>
 *"Job" : {
 *  "name" : string (null) // if null generated from path
 *  "environment"? : EnvironmentConfig,
 *  "cluster"? : Cluster,
 *  "database" : DatabaseConfig,
 *  "reporter"? : Reporter,
 *  "tasks" : [ Task ]
 *}
 * </pre>
 * 
 * @author tatemura
 *
 */
public class Job {
    public static final String TAG_NAME = "job";
    public static final String TASKS = "tasks";
    public static final String CLUSTER = "cluster";
    public static final String ENVIRONMENT = "environment";
    public static final String REPORTER = "reporter";

    public static Job create(File file) {
        JobInfo info = new JobInfo(file.getAbsolutePath());
        return new Job(info,
                ConfigDescription.resolve(file));
    }

    private final JobInfo info;
    private final ConfigValue conf;

    public Job(JobInfo info, ConfigValue conf) {
        this.info = info;
        this.conf = conf;
    }

    public JobInfo getInfo() {
        return info;
    }

    public ConfigValue getConfig() {
        return conf;
    }

    public String getName() {
        String name = conf.find("name");
        if (name != null) {
            return name;
        } else {
            String fname = new File(info.getPath()).getName();
            for (String s : ConfigDescription.SUFFIXES) {
                String suffix = "." + s;
                if (fname.endsWith(suffix)) {
                    return fname.substring(0,
                            fname.length() - suffix.length());
                }
            }
            return fname;
        }
    }

    public EnvironmentConfig createEnv() {
        return conf.getObject(ENVIRONMENT,
                EnvironmentConfig.class, EnvironmentConfig.empty());
    }

    public DatabaseConfig createDb() {
        DatabaseConfig dbconf = conf.getObject(DatabaseConfig.TAG_NAME,
                DatabaseConfig.class);
        dbconf.validate();
        return dbconf;
    }

    public Cluster createCluster() {
        return conf.getObject(CLUSTER, Cluster.class, new Cluster());
    }

    public OutputConfig createOutput() {
        OutputConfig rep = conf.findObject(REPORTER, OutputConfig.class);
        if (rep == null) {
            rep = new OutputConfig();
        }
        String baseDir = info.getOutDir();
        if (baseDir != null) {
            rep.setDstDir(
                    new File(new File(baseDir), "data").getAbsolutePath());
        }

        return rep;
    }

    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<Task>();
        for (ConfigValue v : conf.getValueList(TASKS)) {
            Task task = Task.create(v);
            tasks.add(task);
        }
        return tasks;
    }

}
