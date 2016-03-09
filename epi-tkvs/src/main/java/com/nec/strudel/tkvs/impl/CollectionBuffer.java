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

package com.nec.strudel.tkvs.impl;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;

public interface CollectionBuffer {

    String getName();

    /**
     * loads a key value pair
     * 
     * @param key
     * @param record
     *            the value of the key-value pair. null to indicate that the
     *            key-value has been deleted.
     */
    void load(Key key, @Nullable Record record);

    @Nullable
    Record get(Key key);

    void put(Key key, Record record);

    void delete(Key key);

    Set<Key> getReads();

    /**
     * @return Record in the map is null if it indicates that the record is
     *         deleted
     */
    Map<Key, Record> getWrites();

}
