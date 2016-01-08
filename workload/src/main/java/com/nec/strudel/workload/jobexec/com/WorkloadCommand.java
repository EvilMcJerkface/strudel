package com.nec.strudel.workload.jobexec.com;


import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.out.OutputSet;

public interface WorkloadCommand extends Command {
	OutputSet outputs();
}