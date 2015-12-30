package com.nec.strudel.target;

import com.nec.strudel.Closeable;


public interface TargetLifecycle extends Closeable {

	void operate(String name);
}
