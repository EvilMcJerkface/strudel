package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewUser.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewUser;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ViewUser.class)
public class ViewUserTest extends AbstractInteractionTestBase {
    @Test
    public void testExecute() {
        final User user = new User(1, "user1");

        populate(user);

        Result res = executor()
        		.param(SessionParam.USER_ID, user.getUserId())
        		.executeSuccess();

        assertEquals(user, res.get(OutParam.USER));
    }
	@Test
    public void testPrepare() {
	    final int userId = 1;
    	// prepare state
		State state = newState()
    	.put(SessionParam.USER_ID, userId);
    	// execute prepare
    	Param param = prepare(state);
    	// Check prepare result
        assertEquals(userId,
        		param.getInt(SessionParam.USER_ID));
    }
}
