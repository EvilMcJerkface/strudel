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
package com.nec.strudel.param;

import javax.annotation.Nullable;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;

public class Param {
	public static final String PERM = "perm";
	public static final String RANDOM = "random";
	private RandomConfig randomConf;
	private String value = "";

	public Param(ConfigValue conf) {
		this.randomConf = findConfig(conf);
		if (randomConf == null) {
			this.value = conf.stringValue();
		}
	}
	public Param(String value) {
		this.randomConf = null;
		this.value = value;
	}
	public Param(RandomConfig randomConf) {
		this.randomConf = randomConf;
		this.value = "";
	}
	@Nullable
	public RandomConfig getRandomConf() {
		return randomConf;
	}
	public String getValue() {
		return value;
	}
	public ConfigValue toConfig() {
		if (randomConf == null) {
			return Values.valueOf(value);
		} else {
			String name = (randomConf.isPerm() ? PERM : RANDOM);
			return Values.builder("p")
					.add(name, randomConf)
					.build();
		}
	}
	private static RandomConfig findConfig(ConfigValue vconf) {
		RandomConfig perm = vconf.findObject(PERM, RandomConfig.class);
		if (perm != null) {
			perm.setMode("perm");
			return perm;
		}
		return vconf.findObject(RANDOM, RandomConfig.class);
	}

}