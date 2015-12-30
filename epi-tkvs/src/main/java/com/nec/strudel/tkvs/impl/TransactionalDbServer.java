package com.nec.strudel.tkvs.impl;

import com.nec.strudel.tkvs.TransactionalDB;

public interface TransactionalDbServer<P> {

	TransactionalDB open(P prof);
	void close();
}
