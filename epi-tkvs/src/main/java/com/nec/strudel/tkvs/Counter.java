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

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Counter {
    public static final int INITIAL_VALUE = 1;
    private Key key;
    private int value;

    public Counter(Key key, Record record) {
        this.key = key;
        this.value = toValue(record);
    }

    public Counter(Key key) {
        this.key = key;
        this.value = INITIAL_VALUE;
    }

    public Key getKey() {
        return key;
    }

    public int nextValue() {
        int nextValue = value;
        value++;
        return nextValue;
    }

    public Record getRecord() {
        ByteBuffer buf = ByteBuffer.allocate(INT_SIZE);
        buf.putInt(value);
        return SimpleRecord.create(buf.array());
    }

    static int toValue(Record record) {
        byte[] img = record.toBytes();
        return ByteBuffer.wrap(img).getInt();
    }

    private static final int INT_SIZE = 4;
}
