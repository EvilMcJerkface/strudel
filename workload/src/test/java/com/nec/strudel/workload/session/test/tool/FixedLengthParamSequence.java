package com.nec.strudel.workload.session.test.tool;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.nec.strudel.param.ParamSequence;

public class FixedLengthParamSequence implements ParamSequence {
	private final AtomicInteger remain;
	private final Map<String, Object> param;

	public FixedLengthParamSequence(int count, Map<String, Object> param) {
		this.remain = new AtomicInteger(count);
		this.param = Collections.unmodifiableMap(param);
	}

	@Override
	public Map<String, Object> nextParam(Random rand) {
		if (remain.getAndDecrement() > 0) {
			return param;
		} else {
			return null;
		}
	}

}
