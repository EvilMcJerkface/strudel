package com.nec.strudel.bench.micro.populate;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	PopulateItemTest.class,
	PopulatePostTest.class,
	PopulateSharedTest.class,
	PopulateSubmissionTest.class,
})
public class PopulateTestSuite {

}
