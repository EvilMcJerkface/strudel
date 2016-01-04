package com.nec.strudel.bench.micro.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateShared;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class CreateShared extends AbstractCreateShared<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int setId = param.getInt(InParam.SET_ID);
		String content = param.get(InParam.CONTENT);

		Shared item = new Shared(setId);
		item.setContent(content);
		em.getTransaction().begin();
		em.persist(item);
		em.getTransaction().commit();
		return res.success();
	}

}
