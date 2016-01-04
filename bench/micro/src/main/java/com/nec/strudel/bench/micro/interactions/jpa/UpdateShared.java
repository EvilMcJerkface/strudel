package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateShared;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateShared extends AbstractUpdateShared<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		List<SharedId> ids = param.getObjectList(
				InParam.SHARED_IDS);
		if (ids.isEmpty()) {
			return res.warn("SHARED_ID is not set")
			.failure(ResultMode.MISSING_PARAM);
		}

		String content = param.get(InParam.CONTENT);
		boolean updated = false;
		em.getTransaction().begin();
		for (SharedId id : ids) {
			Shared shared = em.find(Shared.class, id,
					LockModeType.PESSIMISTIC_WRITE);
			if (shared != null) {
				updated = true;
				shared.setContent(content);
			}
		}
		em.getTransaction().commit();
		if (updated) {
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
