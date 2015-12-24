/*******************************************************************************
 *   Copyright 2015 Junichi Tatemura
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.nec.strudel.entity;

import com.nec.strudel.entity.info.EntityInfo;
import com.nec.strudel.entity.key.KeyFinder;



public class IndexType {
	private final String groupName;
	private final boolean inGroup;
	private final String name;
	private final boolean auto;
	private final KeyFinder gkFinder;
	private final EntityInfo target;
	private final Class<?> targetKeyClass;
	private final String property;

	public static IndexType on(Class<?> entityClass, String property) {
		IndexType type = EntityGroup.descriptor(entityClass).indexOn(property);
		if (type == null) {
    		throw new RuntimeException(
    				//TODO refactor
    				"index not defined: " + entityClass + "." + property);
		}
		return type;
	}


	public IndexType(String groupName, String name,
			EntityInfo target,
			KeyFinder gkFinder, On idx) {
		this.groupName = groupName;
		this.name = name;
		this.inGroup = groupName.equals(target.getGroupName());
		this.auto = idx.auto();
		this.gkFinder = gkFinder;
		this.target = target;
		this.targetKeyClass = target.getKeyClass();
		this.property = idx.property();
	}
	protected IndexType(String groupName, String name,
			boolean auto, KeyFinder gkFinder,
			EntityInfo target, String property) {
		this.groupName = groupName;
		this.name = name;
		this.inGroup = groupName.equals(target.getGroupName());;
		this.auto = auto;
		this.gkFinder = gkFinder;
		this.target = target;
		this.targetKeyClass = target.getKeyClass();
		this.property = property;
	}

	public String getName() {
		return name;
	}
	public String getGroupName() {
		return groupName;
	}
	public boolean isAuto() {
		return auto;
	}
	public boolean isInGroup() {
		return inGroup;
	}

	public Object toGroupKey(Object key) {
		return gkFinder.getKey(key);
	}
	public EntityInfo getTarget() {
		return target;
	}
	public Class<?> targetKeyClass() {
		return targetKeyClass;
	}
	public Object getIndexKey(Object entity) {
		return EntityUtil.getProperty(entity, property);
	}
	public KeyFinder getKeyFinder() {
		return KeyFinder.propertyFinder(property);
	}
	protected IndexType unAuto() {
		return new IndexType(groupName, name,
				false, gkFinder, target, property);
	}
	protected String property() {
		return property;
	}
}
