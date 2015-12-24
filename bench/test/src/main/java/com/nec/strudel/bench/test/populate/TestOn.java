package com.nec.strudel.bench.test.populate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nec.strudel.workload.api.Populator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestOn {
	Class<? extends Populator<?, ?>> value();
}
