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

package com.nec.strudel.workload.jobexec;

import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.Workflow;
import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.PopulateTask;
import com.nec.strudel.workload.job.Task;
import com.nec.strudel.workload.job.WorkloadTask;
import com.nec.strudel.workload.jobexec.com.MeasureWorkloadCommand;
import com.nec.strudel.workload.jobexec.com.WorkloadWorkflow;
import com.nec.strudel.workload.measure.MeasurementConfig;

public final class TaskRunnerFactory {

    public static Runnable create(Task task, Job job) {
        if (task instanceof PopulateTask) {
            return PopulateRunner.create((PopulateTask) task, job);
        } else if (task instanceof WorkloadTask) {
            return createWorkloadRunner((WorkloadTask) task, job);
        }
        return null;
    }

    public static Runnable createWorkloadRunner(WorkloadTask work, Job job) {
        Command com = findCommand(work);
        if (com != null) {
            return new WorkloadRunner(work, job, com);
        } else {
            return new Runnable() {
                @Override
                public void run() {
                    WorkloadRunner.logger().info(
                            "nothing to run as a workload");
                }
            };
        }
    }

    private static Command findCommand(WorkloadTask work) {
        MeasurementConfig mconf = work.getConfig().findObject(
                MeasurementConfig.TAG_MEASURE, MeasurementConfig.class);
        if (mconf != null) {
            return MeasureWorkloadCommand.command(mconf);
        }
        Workflow flow = work.getConfig().findObject("process", Workflow.class);
        if (flow != null) {
            return WorkloadWorkflow.command(flow);
        }
        return null;
    }
}
