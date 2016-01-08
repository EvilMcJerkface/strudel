package com.nec.strudel.param;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.session.ParamName;

/**
 * A sequence of the same parameter set.
 * @author tatemura
 *
 */
@ThreadSafe
public class ConstantParamSeq implements ParamSequence {
	private final Map<String, Object> param;
	public ConstantParamSeq(Map<String, Object> param) {
		this.param = Collections.unmodifiableMap(param);
	}
	@Override
	public Map<String, Object> nextParam(Random rand) {
		return param;
	}
	public static Builder builder() {
	    return new Builder();
	}
	public static class Builder {
	    private final Map<String, Object> param =
	            new HashMap<String, Object>();
	    public Builder param(ParamName name, Object value) {
	    	return param(name.name(), value);
	    }
	    public Builder param(String name, Object value) {
	        param.put(name, value);
	        return this;
	    }
	    public ConstantParamSeq build() {
	        return new ConstantParamSeq(param);
	    }
	}
}
