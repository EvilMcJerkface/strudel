package com.nec.strudel.bench.test.populate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;

import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public final class PopulateUtil {
	private PopulateUtil() {
	}

	public static <T, P> void process(Populator<T, P> pop, T db, P param) {
    	pop.process(db, param);
    	boolean success = pop.validate(db, param, new ValidateReporter() {
			@Override
			public void error(String message) {
				Assert.fail("validation failure:"  + message);
			}
		});
    	Assert.assertTrue("validate must return true", success);
	}

	public static ParamBuilder param(int id) {
		return new ParamBuilder(id);
	}
	public static class ParamBuilder {
	    private final int id;
	    private final Random rand;
	    private final Map<String, Object> values =
	    		new HashMap<String, Object>();
	
	    ParamBuilder(int id) {
	        this.id = id;
	        this.rand = new Random();
	    }
	    public ParamBuilder param(String name, Object value) {
	    	this.values.put(name, value);
	    	return this;
	    }

	    public ParamBuilder param(Enum<?> name, Object value) {
	    	this.values.put(name.name(), value);
	    	return this;
	    	
	    }
	    
	    public PopulateParam build() {
	        return new PopulateParam(id, values, rand);
	    }
	    public Random getRandom() {
	    	return rand;
	    }
	    public <T> T createParam(Populator<?, T> pop) {
	    	return pop.createParameter(build());
	    }
	}

}
