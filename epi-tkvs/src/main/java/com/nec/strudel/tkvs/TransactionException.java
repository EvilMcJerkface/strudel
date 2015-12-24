package com.nec.strudel.tkvs;

public class TransactionException extends RuntimeException {


    private static final long serialVersionUID = 1L;

    public TransactionException(String msg) {
        super(msg);
    }

    public TransactionException(String msg, Throwable e) {
        super(msg, e);
    }

}
