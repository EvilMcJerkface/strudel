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
package com.nec.strudel.workload.test;

import com.nec.strudel.workload.session.SessionConfig;

@SuppressWarnings("rawtypes")
public enum SessionFiles implements ResourceFile<SessionConfig> {
	/**
	 * Example of a random session
	 */
	SESSION001("session/session001"),
	/**
	 * Example of a Markov session
	 */
	SESSION002("session/session002");
	
	private final String file;
	SessionFiles(String file) {
		this.file = file;
	}
	public String file() {
		return file;
	}
	public Class<SessionConfig> resourceClass() {
		return SessionConfig.class;
	}


}
