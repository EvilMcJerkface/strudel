package com.nec.strudel.tkvs;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * A transaction on an entity group.
 * @author tatemura
 *
 */
@NotThreadSafe
public interface Transaction {
    String getName();

    Key getKey();
	/**
	 * Gets a key-value pair from a collection.
	 * @param name the name of the key-value collection
	 * @param key the key of the key-value pair
	 * @return null if there is no such key
	 * in the key-value collection.
	 */
	@Nullable
	Record get(String name, Key key);

	/**
	 * Puts a key-value pair into a collection
	 * @param name the name of the key-value collection
	 * @param key the key of the key-value pair
	 * @param value the value of the key-value pair
	 */
	void put(String name, Key key, Record value);

	/**
	 * Deletes a key-value pair from a collection.
	 * @param name the name of the key-value collection.
	 * @param key the key of the key-value pair.
	 */
	void delete(String name, Key key);

	/**
	 * Commits the current transaction. Once it is committed,
	 * this transaction instance should not be used (regardless
	 * of the commit result). In order to retry a transaction,
	 * start a new transaction and redo from the start.
	 * @return true if it is successful; false
	 * if the transaction conflicts with others so
	 * it needs to retry.
	 */
	boolean commit();
}
