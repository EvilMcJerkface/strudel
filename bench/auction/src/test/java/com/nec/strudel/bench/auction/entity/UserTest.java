package com.nec.strudel.bench.auction.entity;



import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;

public class UserTest {

	@Test
	public void testGroupName() {
		EntityDescriptor desc = EntityGroup.descriptor(User.class);
		assertEquals("users", desc.getGroupName());
	}
	@Test
	public void testName() {
		EntityDescriptor desc = EntityGroup.descriptor(User.class);
		assertEquals("users", desc.getName());
	}
}
