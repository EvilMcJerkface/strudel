package com.nec.strudel.tkvs.impl;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;

public interface KVStore {
	Record get(String name, Key key);
}
