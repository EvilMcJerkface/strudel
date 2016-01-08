package com.nec.strudel.workload.session;

public class InteractionConfig {
	private double prob = 0;
	private ThinkTime thinkTime = null;
	public double getProb() {
		return prob;
	}
	public void setProb(double prob) {
		this.prob = prob;
	}
	public ThinkTime getThinkTime() {
		return thinkTime;
	}
	public void setThinkTime(ThinkTime thinkTime) {
		this.thinkTime = thinkTime;
	}
}