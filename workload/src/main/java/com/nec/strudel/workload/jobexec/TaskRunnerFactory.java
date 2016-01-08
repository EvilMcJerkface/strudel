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
		MeasurementConfig m =
				work.getConfig().findObject(
					MeasurementConfig.TAG_MEASURE, MeasurementConfig.class);
		if (m != null) {
			return MeasureWorkloadCommand.command(m);
		}
		Workflow flow = work.getConfig().findObject("process", Workflow.class);
		if (flow != null) {
			return WorkloadWorkflow.command(flow);
		}
		return null;
	}
}
