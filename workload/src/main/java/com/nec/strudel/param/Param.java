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