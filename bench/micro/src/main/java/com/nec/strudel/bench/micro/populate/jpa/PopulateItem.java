package com.nec.strudel.bench.micro.populate.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateItem;
import com.nec.strudel.workload.api.Populator;

public class PopulateItem extends AbstractPopulateItem<EntityManager>
implements Populator<EntityManager, ContentSet> {
	static final String QUERY =
			"SELECT e FROM Item e WHERE e.userId = :uid";
	static final String PARAM_UID = "uid";

	@Override
	public void process(EntityManager db, ContentSet param) {
		int userId = param.getGroupId();
		String[] contents = param.getContents();
		db.getTransaction().begin();
		for (int i = 0; i < contents.length; i++) {
			Item item = new Item(userId);
			item.setContent(contents[i]);
			db.persist(item);
		}
		db.getTransaction().commit();
	}

	@Override
	protected List<Item> getItemsByUser(EntityManager em, int userId) {
		TypedQuery<Item> query = em.createQuery(QUERY, Item.class);
		query.setParameter(PARAM_UID, userId);
		return query.getResultList();
	}
}
