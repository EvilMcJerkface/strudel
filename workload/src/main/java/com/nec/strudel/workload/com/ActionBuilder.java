package com.nec.strudel.workload.com;

import com.nec.congenio.ConfigValue;

public interface ActionBuilder {
	void build(ConfigValue action, CommandBuilder builder);
}