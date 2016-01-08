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
package com.nec.strudel.entity.jpa;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;
import com.nec.strudel.entity.EntityTransaction;

public class EntityTransactionImpl implements EntityTransaction {
	private final EntityManager em;
	public EntityTransactionImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public <T> T get(Class<T> c, Object key) {
		return em.find(c, key, LockModeType.PESSIMISTIC_WRITE);
	}

	@Override
	public void update(Object entity) {
		em.merge(entity);
	}
	@Override
	public void create(Object entity) {
		em.persist(entity);
	}


	@Override
	public void delete(Object entity) {
		try {
			em.remove(entity);
		} catch (IllegalArgumentException e) {
			/**
			 * it may be because entity is detached.
			 * try to find it again (then remove).
			 */
			EntityDescriptor desc =
					EntityGroup.descriptor(entity.getClass());
			Object storedEntity = em.find(entity.getClass(),
					desc.getKey(entity),
					LockModeType.PESSIMISTIC_WRITE);
			if (storedEntity != null) {
				em.remove(storedEntity);
			}
		}
	}
}
