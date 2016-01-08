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
