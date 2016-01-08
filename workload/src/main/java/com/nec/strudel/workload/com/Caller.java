package com.nec.strudel.workload.com;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Caller {

	<T> List<Future<T>> call(List<? extends Callable<T>> calls);

}