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
package com.nec.strudel.jpa;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.target.Target;

public class PersistentStore implements Target<EntityManager> {
	private final EntityManagerFactory factory;
	public PersistentStore(EntityManagerFactory factory) {
		this.factory = factory;
	}

	@Override
	public void close() {
		factory.close();
	}

	@Override
	public EntityManager open() {
		return factory.createEntityManager();
	}
	@Override
	public void beginUse(EntityManager target) {
		/**
		 * EntityManager may have been used by
		 * somebody - clear the internal state
		 * before using.
		 */
		target.clear();
	}
	@Override
	public void endUse(EntityManager target) {
	}

	@Override
	public Instrumented<EntityManager> open(ProfilerService profs) {
		return new Instrumented<EntityManager>() {
			private Profiler prof = new Profiler() {

				@Override
				public JsonObject getValue() {
					return Json.createObjectBuilder().build();
				}
			};
			@Override
			public EntityManager getObject() {
				return factory.createEntityManager();
			}
			@Override
			public Profiler getProfiler() {
				return prof;
			}

		};
	}

}
