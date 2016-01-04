package com.nec.strudel.bench.micro.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateItem;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class CreateItem extends AbstractCreateItem<EntityManager>
implements Interaction<EntityManager> {
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int userId = param.getInt(SessionParam.USER_ID);
		String content = param.get(InParam.CONTENT);
		
		em.getTransaction().begin();
		Item item = new Item(userId);
		item.setContent(content);
		em.persist(item);
		em.getTransaction().commit();

		return res.success();

	}

}
