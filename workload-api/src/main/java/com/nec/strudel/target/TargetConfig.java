package com.nec.strudel.target;

import java.util.Properties;


public interface TargetConfig {
	String getName();
	String getType();
	String getClassName();

	ClassLoader targetClassLoader();

	Properties getProperties();

}
