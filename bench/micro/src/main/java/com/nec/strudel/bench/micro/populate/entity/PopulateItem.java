package com.nec.strudel.bench.micro.populate.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateItem;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.workload.api.Populator;

public class PopulateItem extends AbstractPopulateItem<EntityDB>
implements Populator<EntityDB, ContentSet> {

	@Override
	public void process(EntityDB db, final ContentSet param) {
		final int userId = param.getGroupId();
		db.run(Item.class, userId, new EntityTask<Void>() {
			@Override
			public Void run(EntityTransaction tx) {
				for (String c : param.getContents()) {
					Item item = new Item(userId);
					item.setContent(c);
					tx.create(item);
				}
				return null;
			}
		});
	}

	@Override
	protected List<Item> getItemsByUser(EntityDB db, int userId) {
		return db.getEntitiesByIndex(Item.class,
				"userId", userId);
	}

}
