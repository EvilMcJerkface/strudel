package com.nec.strudel.workload.measure.jmx;

public class MonitorValue {
	private String name;
	private String object;
	private String attr;
	private String aggr;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getAggr() {
		return aggr;
	}
	public void setAggr(String aggr) {
		this.aggr = aggr;
	}
}