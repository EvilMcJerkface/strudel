package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.interactions.base.AbstractListItems;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ListItems extends AbstractListItems<EntityManager>
implements Interaction<EntityManager> {
	static final String QUERY =
			"SELECT e FROM Item e WHERE e.userId = :uid";
	static final String PARAM_UID = "uid";

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int userId = param.getInt(SessionParam.USER_ID);
		TypedQuery<Item> query = em.createQuery(QUERY, Item.class);
		query.setParameter(PARAM_UID, userId);
		List<Item> items = query.getResultList();
		res.set(OutParam.ITEM_LIST, items);
		if (items.isEmpty()) {
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
	}

}
