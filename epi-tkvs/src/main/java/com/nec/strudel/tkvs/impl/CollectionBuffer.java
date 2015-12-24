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
     * @param key
     * @param record the value of the key-value pair.
     * null to indicate that the key-value has been deleted.
     */
    void load(Key key, @Nullable Record record);

    @Nullable
    Record get(Key key);

    void put(Key key, Record record);

    void delete(Key key);

    Set<Key> getReads();

    /**
     * @return Record in the map is null if
     * it indicates that the record is deleted
     */
    Map<Key, Record> getWrites();

}
