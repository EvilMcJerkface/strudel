package com.nec.strudel.tkvs.impl.inmemory;

import java.util.Collection;

import com.nec.strudel.tkvs.impl.CollectionBufferImpl;

public interface Committer {
	boolean commit(long time,
			Collection<CollectionBufferImpl> buffers);
}
