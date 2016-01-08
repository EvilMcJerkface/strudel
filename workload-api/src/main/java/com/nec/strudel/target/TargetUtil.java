/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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
package com.nec.strudel.target;

import com.nec.strudel.instrument.InstrumentUtil;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;

public final class TargetUtil {

	private TargetUtil() {
		// not instantiated
	}
	public static <T> Target<T> sharedTarget(final T obj) {
		return new Target<T>() {

			@Override
			public void close() {
			}

			@Override
			public T open() {
				return obj;
			}

			@Override
			public Instrumented<T> open(
					ProfilerService profs) {
				return InstrumentUtil.uninstrumented(obj);
			}
			@Override
			public void beginUse(T target) {
			}
			@Override
			public void endUse(T target) {
			}
		};
	}
}
