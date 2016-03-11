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

import javax.annotation.Nullable;

import com.nec.strudel.entity.IndexType;
import com.nec.strudel.entity.IsolationLevel;

public final class Indexes {
    private Indexes() {
        // not instantiated
    }

    protected static void store(Transaction tx, IndexData idx) {
        String grpName = idx.getGroupName();
        if (!tx.getName().equals(grpName)) {
            throw new TransactionException(
                    "transaction group mismatch entity="
                            + grpName
                            + " transaction=" + tx.getName());
        }
        if (!tx.getKey().equals(idx.getGroupKey())) {
            throw new TransactionException(
                    "transaction key mismatch entity="
                            + idx.getGroupKey()
                            + " transaction=" + tx.getKey());
        }
        String name = idx.getName();
        tx.put(name, idx.getKey(), idx.toRecord());
    }

    public static IndexData get(TransactionManager db,
            final IndexType type, final Object key) {
        Object groupKey = type.toGroupKey(key);
        String groupName = type.getGroupName();
        return TransactionRunner.run(db,
                /**
                 * In the current implementation an index data on one key is
                 * just one key-value record (so read committed is good enough)
                 */
                IsolationLevel.READ_COMMITTED,
                groupName,
                Entities.toKey(groupKey),
                new TransactionTask<IndexData>() {
                    @Override
                    public IndexData run(Transaction tx) {
                        return get(tx, type, key);
                    }
                });
    }

    private static IndexData get(Transaction tx,
            IndexType type, Object key) {
        Record record = Indexes.getRecord(tx, type, key);
        if (record != null) {
            return IndexData.create(type, tx.getKey(),
                    Entities.toKey(key), record);
        } else {
            return IndexData.create(type, tx.getKey(), Entities.toKey(key));
        }
    }

    public static void remove(TransactionManager db,
            final IndexType type, final Object key, final Object ref) {
        Object groupKey = type.toGroupKey(key);
        String groupName = type.getGroupName();
        TransactionRunner.run(db, groupName,
                Entities.toKey(groupKey),
                new TransactionTask<Void>() {
                    @Override
                    public Void run(Transaction tx) {
                        remove(tx, type, key, ref);
                        return null;
                    }
                });
    }

    public static void remove(Transaction tx,
            final IndexType type, final Object key, final Object ref) {
        IndexData idx = Indexes.get(tx, type, key);
        idx.remove(Entities.toKey(ref));
        store(tx, idx);
    }

    public static void insert(Transaction tx,
            final IndexType type, final Object key, final Object ref) {
        IndexData idx = Indexes.get(tx, type, key);
        idx.insert(Entities.toKey(ref));
        store(tx, idx);
    }

    public static Object newKey(TransactionManager db,
            final IndexType type,
            final Object key) {
        Object grpKey = type.toGroupKey(key);
        String grpName = type.getGroupName();
        return TransactionRunner.run(db, grpName,
                Entities.toKey(grpKey),
                new TransactionTask<Object>() {
                    @Override
                    public Object run(Transaction tx) {
                        return newKey(tx, type, key);
                    }
                });
    }

    public static Object newKey(Transaction tx,
            IndexType type, Object key) {
        IndexData idx = get(tx, type, key);
        Object newKey = idx.createNewKey();
        store(tx, idx);
        return newKey;

    }



    @Nullable
    private static Record getRecord(Transaction tx,
            IndexType type, Object key) {
        String name = type.getName();
        return tx.get(name, Entities.toKey(key));
    }

}
