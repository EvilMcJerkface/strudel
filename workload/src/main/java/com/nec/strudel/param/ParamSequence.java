package com.nec.strudel.param;

import java.util.Map;
import java.util.Random;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public interface ParamSequence {
	Map<String, Object> nextParam(Random rand);
}
