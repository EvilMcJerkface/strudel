/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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
package com.nec.strudel.management.resource;

import java.lang.reflect.Method;

import javax.annotation.Nullable;


public class ResourceAttribute {
	private final String name;
	private String description = "";
	private Method getter;
	private Method setter;
	public ResourceAttribute(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
