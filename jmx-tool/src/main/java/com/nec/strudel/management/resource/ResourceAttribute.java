package com.nec.strudel.management.resource;

import java.lang.reflect.Method;

import javax.annotation.Nullable;


public class ResourceAttribute {
	private final String name;
	private Method getter;
	private Method setter;
	public ResourceAttribute(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	/**
	 * Gets the getter of this attribute
	 * @return null if no getter
	 * is available.
	 */
	@Nullable
	public Method getGetter() {
		return getter;
	}
	public void setGetter(Method getter) {
		this.getter = getter;
	}
	/**
	 * Gets the setter of this
	 * attribute.
	 * @return null if no setter
	 * is available.
	 */
	@Nullable
	public Method getSetter() {
		return setter;
	}
	public void setSetter(Method setter) {
		this.setter = setter;
	}

}
