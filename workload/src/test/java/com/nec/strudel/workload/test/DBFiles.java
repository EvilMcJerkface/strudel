package com.nec.strudel.workload.test;

import com.nec.strudel.target.impl.DatabaseConfig;

public enum DBFiles implements ResourceFile<DatabaseConfig> {
	/**
	 * TKVS Database using in-memory store.
	 */
	DB_TKVS("db001"),

	/**
	 * KVMap Database
	 */
	DB_TEST("db002"),

	DB_JPA("db-jpa");

	private final String file;

	DBFiles(String file) {
		this.file = file;
	}
	public String file() {
		return file;
	}
	@Override
	public Class<DatabaseConfig> resourceClass() {
		return DatabaseConfig.class;
	}
}
