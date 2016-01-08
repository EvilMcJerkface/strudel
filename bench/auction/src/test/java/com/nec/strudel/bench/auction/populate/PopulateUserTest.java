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
package com.nec.strudel.bench.auction.populate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateUser.InParam;
import com.nec.strudel.bench.auction.populate.entity.PopulateUser;
import com.nec.strudel.bench.test.populate.AbstractPopulateTestBase;
import com.nec.strudel.bench.test.populate.PopulateUtil;
import com.nec.strudel.bench.test.populate.TestOn;

@TestOn(PopulateUser.class)
public class PopulateUserTest extends AbstractPopulateTestBase<User> {
    
    @Test
    public void testPopulate() {
        int userId = 1;
        int nameLen = 14;
        User user = PopulateUtil.param(userId)
        		.param(InParam.UNAME_LENGTH.name(), nameLen)
        		.createParam(populator());
        process(user);
        assertEquals(nameLen, user.getUname().length());
        assertGetEquals(user, User.class, userId);
    }

}
