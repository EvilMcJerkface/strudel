package com.nec.strudel.bench.micro.interactions.entity.jpa;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.nec.strudel.entity.jpa.EntityDBImpl;

public class DBTestStore {
	private final EntityManagerFactory emf;
	public static final String DBPATH = "/tmp/testDb";

	public DBTestStore() {
		Properties props = new Properties();
		props.setProperty("javax.persistence.jdbc.url", 
				"jdbc:derby:" + DBPATH + ";create=true");
		props.setProperty("javax.persistence.schema-generation.database.action",
				"drop-and-create");
		emf = Persistence.createEntityManagerFactory("micro", props);
	}

	public void close() {
		emf.close();
	}
	public EntityDBImpl open() {
		EntityManager em = emf.createEntityManager();
		return new EntityDBImpl(em);
	}
}
