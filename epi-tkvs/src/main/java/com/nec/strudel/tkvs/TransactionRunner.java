package com.nec.strudel.tkvs;

import org.apache.log4j.Logger;

import com.nec.strudel.entity.IsolationLevel;

public class TransactionRunner {
    private static final Logger LOGGER =
            Logger.getLogger(TransactionRunner.class);
    private final TransactionalDB db;
    private final String name;
    private final Key key;
    private final IsolationLevel level;
    private final BackoffPolicy backoffPolicy;

    public TransactionRunner(TransactionalDB db, String name, Key key) {
        this.db = db;
        this.name = name;
        this.key = key;
        this.level = db.maxIsolationLevel();
        this.backoffPolicy = db.backoffPolicy();
    }
    public TransactionRunner(TransactionalDB db,
    		IsolationLevel level, String name, Key key) {
        this.db = db;
        this.name = name;
        this.key = key;
        this.level = level;
        this.backoffPolicy = db.backoffPolicy();
    }
    public static <T> T run(TransactionalDB db, String name, Key key,
            TransactionTask<T> t) {
        return new TransactionRunner(db, name, key).run(t);
    }
    public static <T> T run(TransactionalDB db, IsolationLevel level,
    		String name, Key key,
            TransactionTask<T> t) {
        return new TransactionRunner(db, level, name, key).run(t);
    }
    public <T> T run(TransactionTask<T> t) {
        int trial = 0;
        BackoffTime backoff = backoffPolicy.newBackoff();
        boolean waited = false;
        boolean success = false;
        T result = null;
        do {
            Transaction tx = db.start(name, key, level);
            result = t.run(tx);
            success = tx.commit();
            trial++;
            if (!success) {
            	long wait = backoff.failed();
            	if (wait < 0) {
                	throw new TransactionException("transaction retry failed "
                			+ trial + " times: " + name + "@" + key
                			+ " task=" + t);
            	} else if (wait > 0) {
            		waited = true;
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOGGER.error("transaction interrupted");
                        throw new TransactionException(
                               "transaction retry interrupted");
                    }
            	}
            }
        } while (!success);
        if (waited) {
            LOGGER.info("transaction done after " + trial + " trials: "
            		+ name + "@" + key + " task =" + t);
        } else if (trial > 1) {
            LOGGER.debug("transaction done after " + trial + " trials: " + name);
        }
        return result;
    }
}
