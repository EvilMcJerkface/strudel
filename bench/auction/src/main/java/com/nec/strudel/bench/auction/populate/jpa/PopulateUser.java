package com.nec.strudel.bench.auction.populate.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateUser;
import com.nec.strudel.workload.api.Populator;

public class PopulateUser extends AbstractPopulateUser<EntityManager>
implements Populator<EntityManager, User> {

	@Override
	public void process(EntityManager em, User user) {
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
	}

	@Override
	protected User getUser(EntityManager em, int id) {
		return em.find(User.class, id);
	}


}
