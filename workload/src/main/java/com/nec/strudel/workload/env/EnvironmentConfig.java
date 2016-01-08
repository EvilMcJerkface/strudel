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
