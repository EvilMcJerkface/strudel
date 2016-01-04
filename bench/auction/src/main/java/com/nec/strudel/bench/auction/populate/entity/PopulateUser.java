package com.nec.strudel.bench.auction.populate.entity;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateUser;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.workload.api.Populator;

public class PopulateUser extends AbstractPopulateUser<EntityDB>
implements Populator<EntityDB, User> {
	@Override
    public void process(EntityDB edb, User user) {
        edb.create(user);
    }

    @Override
    protected User getUser(EntityDB db, int id) {
    	return db.get(User.class, id);
    }
}
