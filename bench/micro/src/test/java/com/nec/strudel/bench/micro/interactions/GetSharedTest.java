package com.nec.strudel.bench.micro.interactions;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetShared.InParam;
import com.nec.strudel.bench.micro.interactions.entity.GetShared;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(GetShared.class)
public class GetSharedTest extends AbstractInteractionTestBase {
	/**
	 * SHARED_ID is randomly chosen
	 * for a randomly chosen set.
	 */
	@Test
	public void testPrepare() {
		final int userId = 10;
		final int minSetId = 3;
		final int setNum = 2;
		final int minSeqNo = 1;
		final int itemsPerSet = 2;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.MIN_SET_ID, minSetId)
		.put(SessionParam.SET_NUM, setNum)
		.put(SessionParam.MIN_SEQ_NO, minSeqNo)
		.put(SessionParam.ITEMS_PER_SET, itemsPerSet);
    	Param param = prepare(state);
    	SharedId id = param.getObject(InParam.SHARED_ID);
    	assertNotNull(id);
    	assertTrue(minSetId <= id.getSetId());
    	assertTrue(id.getSetId() < minSetId + setNum);
    	assertTrue(minSeqNo <= id.getItemNo());
    	assertTrue(id.getItemNo() < minSeqNo + itemsPerSet);
	}
	@Test
	public void testExecute() {
		final int setId = 12;
		Shared shared = populateNew(Shared.class,
				new ContentBuilder<Shared>("setId", setId));

		Result res = executor()
				.param(InParam.SHARED_ID, shared.getSharedId())
				.executeSuccess();

		EntityAssert.assertEntityEquals(shared,
				res.get(TransitionParam.SHARED));
		EntityAssert.assertEntityEquals(shared,
				complete(res).get(TransitionParam.SHARED));
	}

	@Test
	public void testExecuteEmpty() {
		final SharedId id = new SharedId(3,5);

		Result res = executor()
				.param(InParam.SHARED_ID, id)
				.executeSuccess();

		assertEquals(ResultMode.EMPTY_RESULT, res.getMode());
		assertNull(res.get(TransitionParam.SHARED));
		assertNull(complete(res).get(TransitionParam.SHARED));
	}
}
