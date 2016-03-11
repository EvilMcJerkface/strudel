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

package com.nec.strudel.tkvs;

import java.util.concurrent.ConcurrentHashMap;

import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.IsolationLevel;

public final class Entities {
    private Entities() {
        // not instantiated
    }

    public static <T> T get(Transaction tx, Class<T> cls,
            EntityDescriptor desc, Object key) {
        String name = desc.getName();
        Record record = tx.get(name, toKey(key));
        if (record != null) {
            return create(cls, record);
        } else {
            return null;
        }
    }

    public static <T> T get(TransactionManager db,
            final Class<T> cls, final EntityDescriptor desc,
            Object groupKey, final Object key) {
        String groupName = desc.getGroupName();
        return TransactionRunner.run(db,
                /**
                 * For reading single key-value, read-committed is same as
                 * serializable
                 */
                IsolationLevel.READ_COMMITTED,
                groupName,
                toKey(groupKey),
                new TransactionTask<T>() {
                    @Override
                    public T run(Transaction tx) {
                        return get(tx, cls, desc, key);
                    }
                });
    }

    public static void store(Transaction tx, EntityDescriptor desc,
            Object entity) {
        String grpName = desc.getGroupName();
        Object key = desc.getKey(entity);
        Key grpKey = toKey(desc.toGroupKey(key));
        if (!tx.getName().equals(grpName)) {
            throw new TransactionException(
                    "transaction group mismatch entity="
                            + grpName
                            + " transaction=" + tx.getName());
        }
        if (!tx.getKey().equals(grpKey)) {
            throw new TransactionException(
                    "transaction key mismatch entity="
                            + grpKey
                            + " transaction=" + tx.getKey());
        }
        String name = desc.getName();
        tx.put(name, toKey(key), Entities.toRecord(entity));
    }

    public static void store(TransactionManager db,
            EntityDescriptor desc, final Object entity) {
        String grpName = desc.getGroupName();
        final Object key = desc.getKey(entity);
        Object grpKey = desc.toGroupKey(key);
        final String name = desc.getName();

        TransactionRunner.run(db,
                /**
                 * For updating one key-value, snapshot isolation is same as
                 * serializable
                 */
                IsolationLevel.SNAPSHOT,
                grpName, toKey(grpKey),
                new TransactionTask<Void>() {
                    @Override
                    public Void run(Transaction tx) {
                        tx.put(name, toKey(key),
                                Entities.toRecord(entity));
                        return null;
                    }
                });
    }

    private static <T> T create(Class<T> cls, Record record) {
        EntityConstructor cons = getConstructorOf(cls);
        return cons.create(record);
    }

    private static EntityConstructor getConstructorOf(Class<?> cls) {
        EntityConstructor cons = CONSTS.get(cls);
        if (cons == null) {
            cons = EntityConstructor.of(cls);
            CONSTS.put(cls, cons);
        }
        return cons;
    }

    protected static KeyConstructor getKeyConstructorOf(Class<?> cls) {
        KeyConstructor cons = K_CONSTS.get(cls);
        if (cons == null) {
            cons = KeyConstructor.constructorOf(cls);
            K_CONSTS.put(cls, cons);
        }
        return cons;
    }

    private static final ConcurrentHashMap<Class<?>, EntityConstructor> CONSTS =
            new ConcurrentHashMap<Class<?>, EntityConstructor>();

    private static final ConcurrentHashMap<Class<?>, KeyConstructor> K_CONSTS =
            new ConcurrentHashMap<Class<?>, KeyConstructor>();

    protected static Key toKey(Object key) {
        KeyConstructor cons = getKeyConstructorOf(key.getClass());
        return cons.toKey(key);
    }

    private static Record toRecord(Object entity) {
        EntityConstructor cons = getConstructorOf(
                entity.getClass());
        return cons.toRecord(entity);
    }

    public static void delete(Transaction tx, EntityDescriptor desc, Object key) {
        String name = desc.getName();
        tx.delete(name, toKey(key));
    }

    public static void delete(TransactionManager db, final EntityDescriptor desc,
            final Object groupKey, final Object key) {
        String groupName = desc.getGroupName();
        TransactionRunner.run(db, groupName, toKey(groupKey),
                new TransactionTask<Void>() {
                    @Override
                    public Void run(Transaction tx) {
                        delete(tx, desc, key);
                        return null;
                    }
                });
    }

}
