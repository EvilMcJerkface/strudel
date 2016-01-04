package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.interactions.base.AbstractListShared;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ListShared extends AbstractListShared<EntityManager>
implements Interaction<EntityManager> {
	static final String QUERY = "SELECT s FROM Shared s WHERE s.setId = :sid";
	static final String PARAM_SID = "sid";
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int setId = param.getInt(InParam.SET_ID);
		List<Shared> shareds = em.createQuery(QUERY, Shared.class)
				.setParameter(PARAM_SID, setId)
				.getResultList();
		res.set(OutParam.ENTITY_LIST, shareds);
		if (shareds.isEmpty()) {
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
	}

}
