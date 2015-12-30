package com.nec.strudel.jpa;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.TargetCreator;
import com.nec.strudel.target.TargetLifecycle;

public class PersistenceService implements TargetCreator<EntityManager> {


	@Override
	public PersistentStore create(TargetConfig dbConfig) {
		String unitName = dbConfig.getName();
		Properties props = dbConfig.getProperties();
		/**
		 * NOTE proprietary feature of EclpseLink:
		 */
		props.put(PersistenceUnitProperties.CLASSLOADER,
				dbConfig.targetClassLoader());
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				unitName, props);
		return new PersistentStore(emf);
	}
	@Override
	public TargetLifecycle createLifecycle(TargetConfig dbConfig) {
		return new JPADatabaseCreator(dbConfig);
	}

	@Override
	public Class<?> instrumentedClass(TargetConfig config) {
		return PersistentStore.class;
	}
}
