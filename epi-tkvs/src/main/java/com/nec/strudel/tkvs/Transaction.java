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
import javax.annotation.concurrent.NotThreadSafe;

/**
 * A transaction on an entity group.
 * 
 * @author tatemura
 *
 */
@NotThreadSafe
public interface Transaction {
    String getName();

    Key getKey();

    /**
     * Gets a key-value pair from a collection.
     * 
     * @param name
     *            the name of the key-value collection
     * @param key
     *            the key of the key-value pair
     * @return null if there is no such key in the key-value collection.
     */
    @Nullable
    Record get(String name, Key key);

    /**
     * Puts a key-value pair into a collection
     * 
     * @param name
     *            the name of the key-value collection
     * @param key
     *            the key of the key-value pair
     * @param value
     *            the value of the key-value pair
     */
    void put(String name, Key key, Record value);

    /**
     * Deletes a key-value pair from a collection.
     * 
     * @param name
     *            the name of the key-value collection.
     * @param key
     *            the key of the key-value pair.
     */
    void delete(String name, Key key);

    /**
     * Commits the current transaction. Once it is committed, this transaction
     * instance should not be used (regardless of the commit result). In order
     * to retry a transaction, start a new transaction and redo from the start.
     * 
     * @return true if it is successful; false if the transaction conflicts with
     *         others so it needs to retry.
     */
    boolean commit();
}
