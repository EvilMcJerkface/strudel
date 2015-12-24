package com.nec.strudel.tkvs;

public interface TransactionTask<T> {
    T run(Transaction tx);
}
