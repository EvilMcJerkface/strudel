package com.nec.strudel.tkvs.impl.inmemory;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.impl.KVStore;
import com.nec.strudel.tkvs.impl.TransactionBaseImpl;
import com.nec.strudel.tkvs.impl.TransactionProfiler;

public class InMemoryTransaction extends TransactionBaseImpl {
	private final long time;
	private final Committer com;
    public InMemoryTransaction(String name, Key key,
            KVStore store, long time, Committer com, TransactionProfiler prof) {
    	super(name, key, store, prof);
    	this.time = time;
    	this.com = com;
	}

	@Override
	public boolean commit() {
		return com.commit(time, buffers());
	}

}
