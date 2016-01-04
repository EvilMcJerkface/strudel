package com.nec.strudel.bench.micro.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetShared;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class GetShared extends AbstractGetShared<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		SharedId id = param.getObject(InParam.SHARED_ID);
		if (id == null) {
			return res.warn("SHARED_ID is not set")
			.failure(ResultMode.MISSING_PARAM);
		}
		Shared shared = em.find(Shared.class, id);
		if (shared != null) {
			res.set(TransitionParam.SHARED, shared);
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
