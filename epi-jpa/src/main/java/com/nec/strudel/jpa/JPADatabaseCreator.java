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
