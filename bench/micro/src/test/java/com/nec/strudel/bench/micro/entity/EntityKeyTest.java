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

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.entity.SubmissionId;
import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;

public class EntityKeyTest {

	@Test
	public void testItemId() {
		int uid = 1;
		int ino = 2;
		EntityDescriptor desc = EntityGroup.descriptor(Item.class);
		Item item = new Item();
		item.setUserId(uid);
		item.setItemNo(ino);
		item.setContent("test");
		Object key = desc.getKey(item);
		testKey(key, uid, ino);
	}
	@Test
	public void testPostId() {
		int uid = 1;
		int ino = 2;
		EntityDescriptor desc = EntityGroup.descriptor(Post.class);
		Post item = new Post();
		item.setUserId(uid);
		item.setItemNo(ino);
		item.setContent("test");
		Object key = desc.getKey(item);
		testKey(key, uid, ino);
	}
	void testKey(Object key, int uid, int ino) {
		assertTrue(key instanceof ItemId);
		EntityDescriptor desc = EntityGroup.descriptor(Item.class);
		ItemId iid = (ItemId) key;
		assertEquals(uid, iid.getUserId());
		assertEquals(ino, iid.getItemNo());
		Object gkey = desc.toGroupKey(key);
		assertTrue(gkey instanceof Integer);
		Integer g = (Integer) gkey;
		assertEquals(uid, (int) g);
	}
	@Test
	public void testSharedId() {
		int sid = 1;
		int ino = 4;
		EntityDescriptor desc = EntityGroup.descriptor(Shared.class);
		Shared shared = new Shared();
		shared.setSetId(sid);
		shared.setItemNo(ino);
		shared.setContent("test");
		Object key = desc.getKey(shared);
		assertTrue(key instanceof SharedId);
		SharedId id = (SharedId) key;
		assertEquals(sid, id.getSetId());
		assertEquals(ino, id.getItemNo());
		Object gkey = desc.toGroupKey(key);
		assertTrue(gkey instanceof Integer);
		Integer g = (Integer) gkey;
		assertEquals(sid, (int) g);
	}
	@Test
	public void testItemEntityGroup() {
		assertTrue(EntityGroup.isRoot(Item.class));
		assertEquals("item", EntityGroup.descriptor(Item.class).getGroupName());
	}
	@Test
	public void testPostEntityGroup() {
		assertTrue(EntityGroup.isRoot(Post.class));
		assertEquals("post", EntityGroup.descriptor(Post.class).getGroupName());
	}
	@Test
	public void testSharedEntityGroup() {
		assertTrue(EntityGroup.isRoot(Shared.class));
		assertEquals("shared", EntityGroup.descriptor(Shared.class).getGroupName());
	}

	@Test
	public void testSubmissionId() {
		int receiverId = 1;
		int senderId = 2;
		int submitNo = 10;
		EntityDescriptor desc = EntityGroup.descriptor(Submission.class);
		Submission sub = new Submission();
		sub.setReceiverId(receiverId);
		sub.setSenderId(senderId);
		sub.setSubmitNo(submitNo);
		sub.setContent("test");
		Object key = desc.getKey(sub);
		assertTrue(key instanceof SubmissionId);
		SubmissionId sid = (SubmissionId) key;
		assertEquals(receiverId, sid.getReceiverId());
		assertEquals(senderId, sid.getSenderId());
		assertEquals(submitNo, sid.getSubmitNo());
		Object gkey = desc.toGroupKey(key);
		assertTrue(gkey instanceof Integer);
		Integer g = (Integer) gkey;
		assertEquals(receiverId, (int) g);
	}
}
