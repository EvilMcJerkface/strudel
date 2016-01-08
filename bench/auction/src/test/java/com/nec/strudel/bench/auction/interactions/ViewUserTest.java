/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
