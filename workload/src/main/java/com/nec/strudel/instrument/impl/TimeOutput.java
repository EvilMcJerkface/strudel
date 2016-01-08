package com.nec.strudel.instrument.impl;

import java.util.Arrays;
import java.util.List;

import com.nec.strudel.json.func.Div;
import com.nec.strudel.json.func.Value;

public class TimeOutput {
	public static List<NamedFunc> outputsOf(String name) {
		return new TimeOutput(name).outputs();
	}
	public static String countOf(String name) {
		return name + "_count";
	}
	public static String timeOf(String name) {
		return name + "_time";
	}

	public static String avgOf(String name) {
		return name + "_avg_time";
	}
	private String count;
	private String time;
	private String avg;
	public TimeOutput(String name) {
		this.count = countOf(name);
		this.time = timeOf(name);
		this.avg = avgOf(name);
	}
	public TimeOutput avg(String avg) {
		this.avg = avg;
		return this;
	}
	public TimeOutput count(String count) {
		this.count = count;
		return this;
	}
	public List<NamedFunc> outputs() {
		return Arrays.asList(
				new NamedFunc(count, Value.of(count)),
				new NamedFunc(avg, Div.of(
					Value.of(time),
					Value.of(count)))
				);
	}
}