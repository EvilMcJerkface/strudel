package com.nec.strudel.workload.com;

import java.util.Properties;

public class ProcessCommandConfig {
	private String command = "";
	private String[] args = new String[0];
	private String input = "";
	private Properties env = new Properties();

	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public Properties getEnv() {
		return env;
	}
	public void setEnv(Properties env) {
		this.env = env;
	}
	
}