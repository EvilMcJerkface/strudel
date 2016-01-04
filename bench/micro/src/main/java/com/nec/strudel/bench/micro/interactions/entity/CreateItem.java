package com.nec.strudel.bench.micro.interactions.entity;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateItem;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

/**
 * An interaction that emulate creation of a new private item
 * by a particular user. It requires the following parameters
 * set:
 * <ul>
 * <li> USER_ID
 * <li> CONTENT_LENGTH
 * </ul>
 * It does not change any transitional session parameters.
 * @author tatemura
 *
 */
public class CreateItem extends AbstractCreateItem<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		final int userId = param.getInt(SessionParam.USER_ID);
		final String content = param.get(InParam.CONTENT);
		Item item = new Item(userId);
		item.setContent(content);
		db.create(item);
		return res.success();
	}

}
