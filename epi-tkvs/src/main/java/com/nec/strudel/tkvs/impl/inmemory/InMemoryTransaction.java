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

package com.nec.strudel.tkvs.impl.inmemory;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.impl.KeyValueReader;
import com.nec.strudel.tkvs.impl.TransactionBaseImpl;
import com.nec.strudel.tkvs.impl.TransactionProfiler;

public class InMemoryTransaction extends TransactionBaseImpl {
    private final long time;
    private final Committer com;

    public InMemoryTransaction(String name, Key key,
            KeyValueReader store, long time, Committer com, TransactionProfiler prof) {
        super(name, key, store, prof);
        this.time = time;
        this.com = com;
    }

    @Override
    public boolean commit() {
        return com.commit(time, buffers());
    }

}
