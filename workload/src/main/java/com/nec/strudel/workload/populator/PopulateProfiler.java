/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.workload.populator;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.instrument.OperationListener;

@NotThreadSafe
public class PopulateProfiler {
	private final OperationListener listener;
	private long startNano;

	public PopulateProfiler(OperationListener listener) {
		this.listener = listener;
	}
	public void start() {
		startNano = System.nanoTime();
	}
	public void end() {
		long timeMicro = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startNano);
		listener.operation(timeMicro);
	}
}
