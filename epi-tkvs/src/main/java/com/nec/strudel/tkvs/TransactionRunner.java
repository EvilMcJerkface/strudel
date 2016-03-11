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

import org.apache.log4j.Logger;

import com.nec.strudel.entity.IsolationLevel;

public class TransactionRunner {
    private static final Logger LOGGER = Logger
            .getLogger(TransactionRunner.class);
    private final TransactionManager db;
    private final String name;
    private final Key key;
    private final IsolationLevel level;
    private final BackoffPolicy backoffPolicy;

    public TransactionRunner(TransactionManager db, String name, Key key) {
        this.db = db;
        this.name = name;
        this.key = key;
        this.level = db.maxIsolationLevel();
        this.backoffPolicy = db.backoffPolicy();
    }

    public TransactionRunner(TransactionManager db,
            IsolationLevel level, String name, Key key) {
        this.db = db;
        this.name = name;
        this.key = key;
        this.level = level;
        this.backoffPolicy = db.backoffPolicy();
    }

    public static <T> T run(TransactionManager db, String name, Key key,
            TransactionTask<T> task) {
        return new TransactionRunner(db, name, key).run(task);
    }

    public static <T> T run(TransactionManager db, IsolationLevel level,
            String name, Key key,
            TransactionTask<T> task) {
        return new TransactionRunner(db, level, name, key).run(task);
    }

    public <T> T run(TransactionTask<T> task) {
        int trial = 0;
        BackoffTime backoff = backoffPolicy.newBackoff();
        boolean waited = false;
        boolean success = false;
        T result = null;
        do {
            Transaction tx = db.start(name, key, level);
            result = task.run(tx);
            success = tx.commit();
            trial++;
            if (!success) {
                long wait = backoff.failed();
                if (wait < 0) {
                    throw new TransactionException("transaction retry failed "
                            + trial + " times: " + name + "@" + key
                            + " task=" + task);
                } else if (wait > 0) {
                    waited = true;
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException ex) {
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
                    + name + "@" + key + " task =" + task);
        } else if (trial > 1) {
            LOGGER.debug(
                    "transaction done after " + trial + " trials: " + name);
        }
        return result;
    }
}
