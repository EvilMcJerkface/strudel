package com.nec.strudel.workload.target.test;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.persistence.EntityManager;

import org.junit.Test;

import com.nec.strudel.entity.test.auction.User;
import com.nec.strudel.jpa.PersistenceService;
import com.nec.strudel.jpa.PersistentStore;
import com.nec.strudel.target.DatabaseCreator;
import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.workload.test.DBFiles;
import com.nec.strudel.workload.test.Resources;

public class PersistenceServiceTest {


	@Test
	public void test() {
		TargetConfig dbconf = Resources.create(DBFiles.DB_JPA);
		Properties props = dbconf.getProperties();
		assertNotNull(props.getProperty("javax.persistence.jdbc.url"));
		PersistenceService service = new PersistenceService();
		TargetLifecycle lcm = service.createLifecycle(dbconf);
		lcm.operate(DatabaseCreator.INIT);
		lcm.close();

		PersistentStore store = service.create(dbconf);
		EntityManager em = store.open();
		User user = new User(1);
		user.setName("testuser");
		em.persist(user);
		User user1 = em.find(User.class, 1);
		assertNotNull(user1);
		assertEquals(user.getName(), user1.getName());
		store.close();
	}
}
