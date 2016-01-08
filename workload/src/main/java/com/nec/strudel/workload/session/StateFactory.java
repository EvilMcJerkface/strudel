package com.nec.strudel.workload.session;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.session.impl.State;

public class StateFactory {
    private final Random rand;
    private final ParamSequence params;
    public StateFactory(ParamSequence params, Random rand) {
        this.params = params;
        this.rand = rand;
    }
    @Nullable
    public State next() {
        Map<String, Object> param = params.nextParam(rand);
        if (param != null) {
            return State.newState(param, rand);
        } else {
            return null;
        }
    }
    public Random getRandom() {
    	return rand;
    }
}