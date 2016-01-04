package com.nec.strudel.bench.micro.populate.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateShared;
import com.nec.strudel.workload.api.Populator;

public class PopulateShared extends AbstractPopulateShared<EntityManager>
implements Populator<EntityManager, ContentSet> {
	static final String QUERY =
			"SELECT e FROM Shared e WHERE e.setId = :sid";
	static final String PARAM_SID = "sid";

	@Override
	public void process(EntityManager db, ContentSet param) {
		int setId = param.getGroupId();
		String[] contents = param.getContents();
		db.getTransaction().begin();
		for (int i = 0; i < contents.length; i++) {
			Shared item = new Shared(setId);
			item.setContent(contents[i]);
			db.persist(item);
		}
		db.getTransaction().commit();
	}

	@Override
	protected List<Shared> getSharedBySetId(EntityManager em, int setId) {
		TypedQuery<Shared> query = em.createQuery(QUERY, Shared.class);
		query.setParameter(PARAM_SID, setId);
		return query.getResultList();
	}
}
