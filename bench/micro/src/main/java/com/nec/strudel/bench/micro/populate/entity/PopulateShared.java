package com.nec.strudel.bench.micro.populate.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateShared;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.workload.api.Populator;

public class PopulateShared extends AbstractPopulateShared<EntityDB>
implements Populator<EntityDB, ContentSet> {

	@Override
	public void process(EntityDB db, final ContentSet param) {
		final int setId = param.getGroupId();
		db.run(Shared.class, setId, new EntityTask<Void>() {
			@Override
			public Void run(EntityTransaction tx) {
				for (String c : param.getContents()) {
					Shared shared = new Shared(setId);
					shared.setContent(c);
					tx.create(shared);
				}
				return null;
			}
		});
	}

	@Override
	protected List<Shared> getSharedBySetId(EntityDB db, int setId) {
		return db.getEntitiesByIndex(Shared.class,
				"setId", setId);
	}
}
