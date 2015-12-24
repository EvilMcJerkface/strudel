package com.nec.strudel.tkvs;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.entity.IsolationLevel;


/**
 * A Key-Value Store that supports entity-group transactions.
 * @author tatemura
 *
 */
@NotThreadSafe
public interface TransactionalDB {

	/**
	 * Starts a transaction over one entity group.
	 * Multiple transactions can be started concurrently.
	 * @param groupName the name of the entity group
	 * @param groupKey the key of the entity group
	 * @return a transaction.
	 */
	Transaction start(String groupName, Key groupKey);

	Transaction start(String groupName, Key groupKey, IsolationLevel level);

	IsolationLevel maxIsolationLevel();

	/**
	 * A policy for transaction retry
	 * @return backoff policy
	 */
	BackoffPolicy backoffPolicy();
	/**
	 * Gets the name of the database.
	 * @return the name of the database.
	 */
	String getName();

	void close();
}
