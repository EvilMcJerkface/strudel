package com.nec.strudel.bench.micro.interactions.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateShared;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateShared extends AbstractUpdateShared<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		final List<SharedId> ids = param.getObjectList(
				InParam.SHARED_IDS);
		if (ids.isEmpty()) {
			return res.warn("SHARED_ID is not set")
			.failure(ResultMode.MISSING_PARAM);
		}

		final String content = param.get(InParam.CONTENT);
		boolean updated = db.run(Shared.class, ids.get(0),
				new EntityTask<Boolean>() {
			@Override
			public Boolean run(EntityTransaction tx) {
				boolean hasUpdate = false;
				for (SharedId id : ids) {
					Shared shared =
						tx.get(Shared.class, id);
					if (shared != null) {
						shared .setContent(content);
						tx.update(shared);
						hasUpdate = true;
					}
				}
				return hasUpdate;
			}
		});
		if (updated) {
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
