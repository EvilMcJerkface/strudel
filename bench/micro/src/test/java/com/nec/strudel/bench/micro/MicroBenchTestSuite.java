package com.nec.strudel.bench.micro;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.nec.strudel.bench.micro.entity.EntityTestSuite;
import com.nec.strudel.bench.micro.interactions.InteractionTestSuite;
import com.nec.strudel.bench.micro.populate.PopulateTestSuite;


@RunWith(Suite.class)

@Suite.SuiteClasses({
	EntityTestSuite.class,
	InteractionTestSuite.class,
	PopulateTestSuite.class,
})
public class MicroBenchTestSuite {

}
