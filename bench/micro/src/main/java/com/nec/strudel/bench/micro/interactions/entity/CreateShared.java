package com.nec.strudel.bench.micro.interactions.entity;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateShared;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class CreateShared extends AbstractCreateShared<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int setId = param.getInt(InParam.SET_ID);
		Shared shared = new Shared(setId);
		shared.setContent(param.get(InParam.CONTENT));
		db.create(shared);
		return res.success();
	}

}
