package com.nec.strudel.workload.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

public class OutputSet {
	private final List<String> names;
	private final Map<String, Output> outs;

	public OutputSet(List<String> names,
			Map<String, Output> outs) {
		this.names = names;
		this.outs = outs;
	}

	public List<String> names() {
		return names;
	}
	public Output output(String name) {
		Output out = outs.get(name);
		if (out != null) {
			return out;
		} else {
			return Output.empty();
		}
	}
	public Output flatten() {
		Output.Builder b = Output.builder();
		for (String name : names) {
			b.add(outs.get(name));
		}
		return b.build();
	}
	public static Builder builder() {
		return new Builder();
	}
	public static OutputSet empty() {
		return builder().build();
	}

	public static class Builder {
		private List<String> names = new ArrayList<String>();
		private final Map<String, Output> outputs =
				new HashMap<String, Output>();
		public Builder add(String name, Output output) {
			names.add(name);
			outputs.put(name, output);
			return this;
		}
		public Builder add(String name, JsonObject output) {
			return add(name, Output.valueOf(output));
		}
		public Builder add(OutputSet out) {
			for (String name : out.names()) {
				add(name, out.output(name));
			}
			return this;
		}
		public OutputSet build() {
			return new OutputSet(names, outputs);
		}
	}
}
