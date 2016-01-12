package com.nec.strudel.workload.session.test.tool;

import java.util.concurrent.atomic.AtomicInteger;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;
import com.nec.strudel.session.StateParam;

public class IncrementCount implements Interaction<AtomicInteger> {
	public enum TestParam implements StateParam {
		COUNT;
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(TestParam.COUNT);
	}

	@Override
	public Result execute(Param param, AtomicInteger db, ResultBuilder res) {
		int count = param.getInt(TestParam.COUNT);
		res.set(TestParam.COUNT, count + 1);
		db.incrementAndGet();
		return res.success();
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.export(TestParam.COUNT);
	}
	
}
