package com.nec.strudel.workload.com;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.log4j.Logger;

@ThreadSafe
public interface CommandContext extends Caller {

	Logger logger();

}
