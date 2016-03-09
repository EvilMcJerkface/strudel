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

package com.nec.strudel.entity;

import java.util.List;

import javax.annotation.Nullable;

public interface EntityDB {

    /**
     * Gets an entity by key
     * 
     * @param cls the class of the entity.
     * @param key the key that refers to the entity.
     * @return an entity. null if there is no entity with the given key.
     */
    @Nullable
    <T> T get(Class<T> cls, Object key);

    /**
     * Updates an entity
     * 
     * @param entity
     */
    void update(Object entity);

    /**
     * Creates an entity.
     * 
     * @param entity
     */
    void create(Object entity);

    /**
     * Deletes an entity
     * 
     * @param entity
     */
    void delete(Object entity);

    /**
     * Runs a transaction on an entity
     * 
     * @param entity
     * @param task
     * @return
     */
    <T> T run(Object entity, EntityTask<T> task);

    /**
     * Runs a transaction on an entity
     * 
     * @param entityClass
     *            the class of the entity
     * @param key
     *            the key of the entity
     * @param task
     *            transaction to run
     * @return the return value of the transaction
     */
    <T> T run(Class<?> entityClass, Object key, EntityTask<T> task);

    /**
     *
     * Gets a list of entities by an index
     * 
     * @param ec
     * @param c
     * @param key
     * @return a list of entities. an empty list if no entity matches.
     */
    <T> List<T> getEntitiesByIndex(Class<T> ec, String property,
            Object key);

    /**
     * Scans key of entities in an index
     * 
     * @param entityClass
     * @param property
     * @param key
     */
    <T> Iterable<T> scanIds(Class<T> idClass,
            Class<?> entityClass, String property,
            Object key);

}