package com.nec.strudel.bench.test;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Test databases with JPA. By default it uses Derby.
 * <ul>
 * <li> default database files for Derby is /tmp/testDb
 * <li> to use a different location use property test.derby.data as
 * mvn test -DargLine="-Dtest.derby.data=/tmp/derby-test"
 * </ul>
 * To test with MySQL:
 * <ol>
 * <li> make MySQL available for user=root, password="" and create database
 * with the name specified as the unit name.
 * <li> do: mvn test -DargLine="-Dtest.mysql=localhost" or specify a remote hostname.
 * <li> for a different user or password use: test.mysql.user and test.mysql.password,
 * respectively
 * </ol>
 */
public class JpaDatabase {
	public static final String DBPATH = "/tmp/testDb";
	private final String name;
	private EntityManagerFactory emf;
	public JpaDatabase(String name) {
		this.name = name;
	}
	public void startup() {
		Properties props = new Properties();
		String mysql = System.getProperty("test.mysql");
		if (mysql != null) {
			String user = System.getProperty("test.mysql.user", "root");
			String pass = System.getProperty("test.mysql.password", "");
			props.setProperty("javax.persistence.driver", "com.mysql.jdbc.Driver");
			props.setProperty("javax.persistence.jdbc.user", user);
			props.setProperty("javax.persistence.jdbc.password", pass);
			props.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://"
					+ mysql + "/micro");
		} else { // use Derby:
			String path = System.getProperty("test.derby.data", DBPATH);
			props.setProperty("javax.persistence.jdbc.url",
					"jdbc:derby:" + path + ";create=true");
		}
		props.setProperty("javax.persistence.schema-generation.database.action",
				"drop-and-create");



		emf = Persistence.createEntityManagerFactory(name, props);
	}
	public EntityManagerFactory getFactory() {
		return emf;
	}
	public EntityManager createEntityManager() {
		return emf.createEntityManager();
	}
	public void close() {
		if (emf != null) {
			emf.close();
		}
	}
}