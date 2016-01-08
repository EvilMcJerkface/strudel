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

import javax.persistence.EntityManager;

import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.jpa.EntityDBImpl;
import com.nec.strudel.instrument.InstrumentUtil;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.TargetCreator;
import com.nec.strudel.target.TargetLifecycle;

/**
 * Creator of EntityDB based on JPA.
 *
 */
public class EntityPersistenceService implements TargetCreator<EntityDB> {

	@Override
	public Target<EntityDB> create(TargetConfig config) {
		return new EntityStore(
				new PersistenceService().create(config));
	}

	@Override
	public TargetLifecycle createLifecycle(TargetConfig config) {
		return new JPADatabaseCreator(config);
	}

	@Override
	public Class<?> instrumentedClass(TargetConfig config) {
		return new PersistenceService().instrumentedClass(config);
	}

	static class EntityStore implements Target<EntityDB> {
		private final Target<EntityManager> store;
		EntityStore(Target<EntityManager> store) {
			this.store = store;
		}
		@Override
		public void close() {
			store.close();
		}

		@Override
		public EntityDB open() {
			return new EntityDBImpl(store.open());
		}

		@Override
		public void beginUse(EntityDB target) {
			/**
			 * Entity manager may be used by somebody before.
			 * clear the internal state before using.
			 */
			((EntityDBImpl) target).getEntityManager().clear();
		}
		@Override
		public void endUse(EntityDB target) {
		}
		@Override
		public Instrumented<EntityDB> open(ProfilerService profs) {
			/**
			 * TODO implement EntityDBImpl that
			 * measures statistics.
			 */
			return InstrumentUtil.uninstrumented(open());
		}
		
	}

}
