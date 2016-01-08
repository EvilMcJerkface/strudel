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
package com.nec.strudel.bench.micro.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.PairId;
import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.entity.IndexType;

public class IndexTest {

	@Test
	public void testSubmitPairIndex() {
		IndexType type = IndexType.on(Submission.class, "pairId");
		Submission sub = new Submission();
		sub.setReceiverId(1);
		sub.setSenderId(2);
		sub.setSubmitNo(1);
		sub.setContent("test");
		Object idxKey = type.getIndexKey(sub);
		PairId pid = (PairId) idxKey;
		assertEquals(sub.getReceiverId(), pid.getReceiverId());
		assertEquals(sub.getSenderId(), pid.getSenderId());
		assertEquals(sub.getReceiverId(), type.toGroupKey(pid));
	}
}
