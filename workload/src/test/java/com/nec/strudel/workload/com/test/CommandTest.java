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
package com.nec.strudel.workload.com.test;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;
import com.nec.strudel.workload.com.ProcessCommandConfig;
import com.nec.strudel.workload.test.ResourceNames;
import com.nec.strudel.workload.test.Resources;

public class CommandTest {

	@Test
	public void testCommandCreation() {
		ConfigValue[] args = {
				arg("a1"),
				arg("a2"),
		};
		ConfigValue value = Values.builder("test")
		.add("command", "test").add("args", args)
		.build();
		ProcessCommandConfig spec =
				value.toObject(ProcessCommandConfig.class);
		assertEquals("test", spec.getCommand());
		assertArrayEquals(new String[]{"a1","a2"}, spec.getArgs());
		assertEquals("", spec.getInput());
		assertTrue(spec.getEnv().isEmpty());
	}
	@Test
	public void testReadCommand() {
		ProcessCommandConfig spec = Resources.create(ResourceNames.COMMAND001);
		Properties prop = spec.getEnv();
		assertEquals(2, prop.size());
		assertEquals("/tmp", prop.getProperty("test.dir"));
	}

	ConfigValue arg(String value) {
		return Values.valueOf(value);
	}
}
