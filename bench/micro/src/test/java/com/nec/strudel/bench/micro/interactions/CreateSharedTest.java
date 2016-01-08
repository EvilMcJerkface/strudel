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
package com.nec.strudel.bench.micro.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateShared.InParam;
import com.nec.strudel.bench.micro.interactions.entity.CreateShared;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(CreateShared.class)
public class CreateSharedTest extends AbstractInteractionTestBase {

	@Test
	public void testPrepare() {
		final int userId = 10;
		final int length = 20;
		final int minSetId = 1;
		final int setNum = 2;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.CONTENT_LENGTH, length)
		.put(SessionParam.MIN_SET_ID, minSetId)
		.put(SessionParam.SET_NUM, setNum);
    	Param param = prepare(state);
    	assertNotNull(param.getObject(InParam.SET_ID));
    	int setId = param.getInt(InParam.SET_ID);
    	assertTrue(minSetId <= setId);
    	assertTrue(setId < minSetId + setNum);
    	
    	String content = param.get(InParam.CONTENT);
    	assertNotNull(content);
    	assertEquals(length, content.length());
	}

	@Test
	public void testExecute() {
		final int setId = 11;
		final String content = "ccccc";

		executor()
		.param(InParam.SET_ID, setId)
		.param(InParam.CONTENT, content)
		.executeSuccess();

		Shared item = getSingle(Shared.class, "setId", setId);
		assertEquals(setId, item.getSetId());
		assertEquals(content, item.getContent());
	}
}
