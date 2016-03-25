package com.nec.strudel.workload.jobexec;

import java.io.File;
import java.util.Arrays;

import com.nec.congenio.ConfigCli;
import com.nec.congenio.ConfigCli.Opt;
import com.nec.congenio.exec.ValueHandler;
import com.nec.strudel.workload.job.JobInfo;
import com.nec.strudel.workload.job.JobSuite;

public class JobCli {
    public static final String RUN_COMMAND = "run";
    public static final String SHOW_COMMAND = "show";

    private final String name;

    public JobCli(String name) {
        this.name = name;
    }

    public void execute(String... args) throws Exception {
        execute(createHandler(args), args);
    }

    public void execute(ValueHandler handler, String... args) throws Exception {
        execCli().execute(handler, args);
    }

    public void show(String...args) throws Exception {
        showCli().execute(args);
    }

    protected ConfigCli execCli() {
        ConfigCli cli = new ConfigCli(name + " " + RUN_COMMAND);
        cli.resetOptions();
        cli.enableOptions(Opt.INDEX, Opt.LIB);
        cli.setBaseDescription(JobSuite.baseDescription());
        return cli;
    }

    protected ConfigCli showCli() {
        ConfigCli cli = new ConfigCli(name + " " + SHOW_COMMAND);
        cli.setBaseDescription(JobSuite.baseDescription());
        return cli;
        
    }

    protected ValueHandler createHandler(String[] args) {
        File jobFile = new File(args[args.length - 1]);
        JobInfo info = new JobInfo(jobFile.getAbsolutePath());
        return new JobHandler(info);
    }

    public static void main(String[] args) throws Exception {
        if (args.length <= 1) {
            System.err.println("usage: strudel (run|show) [OPTION] JOBFILE");
            return;
        }
        String cmd = args[0];
        String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);
        if (RUN_COMMAND.equals(cmd)) {
            new JobCli("strudel").execute(cmdArgs);
        } else if (SHOW_COMMAND.equals(cmd)) {
            new JobCli("strudel").show(cmdArgs);
        } else {
            System.err.println("unknown command: " + cmd);
        }
    }

}
