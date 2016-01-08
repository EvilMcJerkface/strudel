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
package com.nec.strudel.workload.session.runner;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.runner.event.SemiOpenSessionExecFactory;

public final class SessionExecs {

	private SessionExecs() {
		// not instantiated
	}
	public static <T> SessionExecFactory<T> createFactory(
			String type,
			String classPath) {
		if (SimpleClosedSessionExecFactory.TYPE.equals(type)) {
			return new SimpleClosedSessionExecFactory<T>();
		} else if (SemiOpenSessionExecFactory.TYPE.equals(type)) {
			return new SemiOpenSessionExecFactory<T>();
		} else {
			throw new ConfigException("unknown session execType: " + type);
		}
	}

	public static <T> SessionExecFactory<T> createFactory(
			SessionConfig<T> conf,
			String classPath) {
		if (conf.getSessionConcurrency() > 0) {
			return new SemiOpenSessionExecFactory<T>();
		}
		return new SimpleClosedSessionExecFactory<T>();
	}
	

}
