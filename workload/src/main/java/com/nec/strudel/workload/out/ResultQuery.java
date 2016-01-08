package com.nec.strudel.workload.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.nec.strudel.json.func.Func;

public class ResultQuery {
	public static ResultQuery query() {
		return new ResultQuery();
	}
	private final List<Output> outputs =
			new ArrayList<Output>();
	public ResultQuery() {
	}
	public ResultQuery output(Output... outs) {
		for (Output out : outs) {
			outputs.add(out);
		}
		return this;
	}
	public JsonObject execute(JsonObject source) {
		JsonObjectBuilder dst = Json.createObjectBuilder();
		for (Output out : outputs) {
			for (Map.Entry<String, Func> e : out.entries()) {
				e.getValue().output(dst, e.getKey(), source);
			}
		}
		return dst.build();
	}

}
