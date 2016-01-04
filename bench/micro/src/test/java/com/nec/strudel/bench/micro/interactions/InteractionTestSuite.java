package com.nec.strudel.bench.micro.interactions;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	GetItemTest.class,
	ListItemsTest.class,
	CreateItemTest.class,
	UpdateItemTest.class,

	GetPostTest.class,
	GetMyPostTest.class,
	ListPostsTest.class,
	ListMyPostsTest.class,
	CreatePostTest.class,
	UpdatePostTest.class,

	GetSharedTest.class,
	ListSharedTest.class,
	CreateSharedTest.class,
})
public class InteractionTestSuite {

}
