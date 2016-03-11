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

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;
import com.nec.strudel.entity.EntityTask;

public class EntityDbImpl implements EntityDB {
    private final EntityManager em;
    private final IndexQuery iq;

    public EntityDbImpl(EntityManager em) {
        this.em = em;
        this.iq = new IndexQuery(em);
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public <T> T get(Class<T> cls, Object key) {
        return em.find(cls, key);
    }

    @Override
    public void update(Object entity) {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    @Override
    public void create(Object entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public void delete(Object entity) {
        /**
         * NOTE assume that this entity is not attached.
         */
        EntityDescriptor desc = EntityGroup.descriptor(entity.getClass());
        em.getTransaction().begin();
        Object storedEntity = em.find(entity.getClass(),
                desc.getKey(entity));
        if (storedEntity != null) {
            em.remove(storedEntity);
        }
        em.getTransaction().commit();
    }

    @Override
    public <T> T run(Object entity, EntityTask<T> task) {
        return run(task);
    }

    @Override
    public <T> T run(Class<?> entityClass, Object key, EntityTask<T> task) {
        return run(task);
    }

    private <T> T run(EntityTask<T> task) {
        try {
            em.getTransaction().begin();
            T res = task.run(new EntityTransactionImpl(em));
            em.getTransaction().commit();
            return res;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    @Override
    public <T> List<T> getEntitiesByIndex(Class<T> ec, String property,
            Object key) {
        return iq.queryByIndex(ec, property, key);
    }

    @Override
    public <T> Iterable<T> scanIds(Class<T> idClass,
            Class<?> entityClass, String property,
            Object key) {
        return iq.scanIdByIndex(idClass, entityClass, property, key);
    }

    public void close() {
        em.close();
    }
}
