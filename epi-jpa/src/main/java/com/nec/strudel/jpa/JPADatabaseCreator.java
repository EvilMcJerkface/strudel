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
package com.nec.strudel.jpa;

import java.util.Properties;

import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.nec.strudel.target.DatabaseCreator;
import com.nec.strudel.target.TargetConfig;

public class JPADatabaseCreator extends DatabaseCreator {
	public static final String PROP_SCHEMA_ACTION =
			"workload.jpa.schema-generation.database.action";
	private final String name;
	private final Properties props;

	public JPADatabaseCreator(TargetConfig dbConfig) {
		this.props = new Properties();
		props.putAll(dbConfig.getProperties());

		/**
		 * NOTE: we did not want let dbConfig directly specify
		 * javax.persistence.* since the dbConfig is shared by the
		 * workers (where we do not want to let them run schema actions).
		 * So we use another property to let the database creator know
		 * the option.
		 */
		String action = props.getProperty(PROP_SCHEMA_ACTION, "drop-and-create");
		props.setProperty("javax.persistence.schema-generation.database.action",
				action);
		/**
		 * NOTE proprietary feature of EclpseLink:
		 */
		props.put(PersistenceUnitProperties.CLASSLOADER,
				dbConfig.targetClassLoader());
		this.name = dbConfig.getName();
	}

	@Override
	public void close() {
	}

	@Override
	public void initialize() {
		Persistence.generateSchema(name, props);
	}

	@Override
	public void prepare() {
	}

}
