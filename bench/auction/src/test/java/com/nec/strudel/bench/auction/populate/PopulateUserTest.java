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
