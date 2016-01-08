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
package com.nec.strudel.tkvs.store;

import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.target.Target;
import com.nec.strudel.tkvs.EntityDBImpl;
import com.nec.strudel.tkvs.TransactionalDB;

public class EntityStore implements Target<EntityDB> {
	private final Target<TransactionalDB> tkvStore;
	public EntityStore(Target<TransactionalDB> tkvStore) {
		this.tkvStore = tkvStore;
	}

	@Override
	public EntityDB open() {
		return new EntityDBImpl(tkvStore.open());
	}

	@Override
	public Instrumented<EntityDB> open(ProfilerService profs) {
		return new ConnectionWrapper(tkvStore.open(profs));
	}

	@Override
	public void close() {
		tkvStore.close();
	}
	@Override
	public void beginUse(EntityDB target) {
	}
	@Override
	public void endUse(EntityDB target) {
	}

	private static class ConnectionWrapper
	implements Instrumented<EntityDB> {
		private final Instrumented<TransactionalDB> con;
		ConnectionWrapper(Instrumented<TransactionalDB> con) {
			this.con = con;
		}

		@Override
		public EntityDB getObject() {
			return new EntityDBImpl(con.getObject());
		}
		@Override
		public Profiler getProfiler() {
			return con.getProfiler();
		}

	}

}
