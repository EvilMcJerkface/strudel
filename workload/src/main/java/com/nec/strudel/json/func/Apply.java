package com.nec.strudel.json.func;

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class Apply implements Func {
	private final Func func;
	private final Func[] argFunc;
	public Apply(Func func, Func... argFunc) {
		this.func = func;
		this.argFunc = argFunc;
	}

	@Override
	public JsonValue get(JsonValue... input) {
		return func.get(computeArg(input));
	}

	JsonValue[] computeArg(JsonValue... input) {
		JsonValue[] args = new JsonValue[argFunc.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = argFunc[i].get(input);
		}
		return args;
	}

	@Override
	public void output(JsonObjectBuilder out,
			String name, JsonValue... input) {
		func.output(out, name, computeArg(input));
	}

}
