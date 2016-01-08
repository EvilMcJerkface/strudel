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
package com.nec.strudel.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.GeneratedValue;
import com.nec.strudel.entity.info.BeanInfo;
import com.nec.strudel.entity.info.EntityInfo;
import com.nec.strudel.entity.key.KeyFinder;
import com.nec.strudel.entity.key.KeySetter;



/**
 *
 *
 */
public class EntityDescriptor {
	private final Class<?> entityClass;
	private final Class<?> keyClass;
	private final String name;
	private final String groupName;
	private final KeyFinder pkFinder;
	private final KeyFinder gkFinder;
	private final KeySetter keySetter;
	private final Map<String, IndexType> indexes;
	private final boolean hasGeneratedGroupKey;
	private final EntityInfo info;
	@Nullable
	private final KeyGeneratorType keyGen;
	public EntityDescriptor(Class<?> entityClass) {
		this.entityClass = entityClass;
		this.info = new EntityInfo(entityClass);
		this.name = info.getName();
		this.groupName = info.getGroupName();
		this.keyClass = info.getKeyClass();
		this.pkFinder = keyFinderFor(info);
		this.gkFinder = gKeyFinderFor(info);
		this.keySetter = keySetterFor(info);
		this.indexes = createIndex(info);
		this.hasGeneratedGroupKey = hasGeneratedGroupKey(info);
		/**
		 * TODO refactor: this depends on the above
		 * final fields initialized.
		 */
		this.keyGen = createKeyGeneratorType(info);
	}
	public String getName() {
		return name;
	}
	public String getGroupName() {
		return groupName;
	}
	public Class<?> getKeyClass() {
		return keyClass;
	}
	public Class<?> getEntityClass() {
		return entityClass;
	}
	public Object getKey(Object entity) {
		return pkFinder.getKey(entity);
	}
	public void setKey(Object entity, Object key) {
		keySetter.setKey(entity, key);
	}

	public Object toGroupKey(Object key) {
		if (keyClass.isInstance(key)) {
			return gkFinder.getKey(key);
		} else {
			return key; // TODO check if this is GKEY
		}
	}
	public boolean hasGeneratedGroupKey() {
		return hasGeneratedGroupKey;
	}
	@Nullable
	public IndexType indexOn(String property) {
		return indexes.get(property);
	}
	@Nullable
	public IndexType autoIndex() {
		for (IndexType idx : indexes.values()) {
			if (idx.isAuto()) {
				return idx;
			}
		}
		return null;
	}
	@Nullable
	public KeyGeneratorType keyGenerator() {
		return keyGen;
	}
	public List<IndexType> externalIndex() {
		List<IndexType> list = new ArrayList<IndexType>();
		for (IndexType idx : indexes.values()) {
			if (!idx.isInGroup()) {
				list.add(idx);
			}
		}
		return list;
	}
	public boolean hasExternalIndex() {
		for (IndexType idx : indexes.values()) {
			if (!idx.isInGroup()) {
				return true;
			}
		}
		return false;
	}
	public List<IndexType> index() {
		return new ArrayList<IndexType>(indexes.values());
	}
	public List<String> properties(Class<? extends Annotation> ann) {
		List<String> keys = new ArrayList<String>();
		for (Field f : entityClass.getDeclaredFields()) {
			if (f.isAnnotationPresent(ann)) {
				keys.add(f.getName());
			}
		}
		return keys;
	}
	/**
	 * Gets a list of property names: (1) when Column is specified
	 * at fields, they are chosen. (2) if no Column exists, all the
	 * fields are chosen.
	 * @return an array of property names.
	 */
	public List<String> properties() {
		return info.properties();
	}

	@Nullable
	private KeyGeneratorType createKeyGeneratorType(EntityInfo info) {
		String genKey = generatedKey(info);
		if (genKey == null) {
			return null;
		}
		IndexType auto = this.autoIndex();
		boolean hasExternal = this.hasExternalIndex();
		/**
		 * NOTE if the auto index is in-group but there are
		 * external indexes, the auto index cannot be used
		 * as a counter because the external indexes need
		 * a generated value that is committed. In such a case,
		 * use an external counter for key generation.
		 * Then, auto index is no longer "auto"
		 * so change it to a regular index.
		 */
		if (auto != null && auto.isInGroup() && hasExternal) {
			indexes.put(auto.property(), auto.unAuto());
			return KeyGeneratorType.outofGroup(info, auto.getKeyFinder(),
					KeySetter.createSetter(info, genKey));
		}
		/**
		 * TODO FIXME if the key needs generated but there is no
		 * auto index, generate a counter
		 */
		return null;
	}

	@Nullable
	static String generatedKey(EntityInfo info) {
		for (String k : info.key()) {
			Field f = info.findField(k);
			if (f.isAnnotationPresent(GeneratedValue.class)) {
				return k;
			}
		}
		return null;
	}

	static boolean hasGeneratedGroupKey(EntityInfo info) {
		for (String gk : info.groupKey()) {
			Field f = info.findField(gk);
			if (f.isAnnotationPresent(GeneratedValue.class)) {
				return true;
			}
		}
		return false;
	}
	static KeyFinder keyFinderFor(EntityInfo info) {
		List<String> ids = info.key();
		if (ids.isEmpty()) {
			throw new RuntimeException("no @Id found:"
			+ info.entityClass());
		}
		return KeyFinder.finderFor(info, info.getKeyClass(), ids);
	}
	static KeyFinder gKeyFinderFor(EntityInfo info) {
		List<String> ids = info.key();
		List<String> gids = info.groupKey();
		Class<?> keyClass = info.getKeyClass();
		if (gids.size() == ids.size()) {
			//KEY = GKEY
			return KeyFinder.identity(keyClass);
		}
		BeanInfo keyInfo = new BeanInfo(keyClass);
		return KeyFinder.finderFor(keyInfo, info.getGroupKeyClass(), gids);
	}


	static Map<String, IndexType> createIndex(EntityInfo info) {
		Map<String, IndexType> map = new HashMap<String, IndexType>();
		Indexes idxes = info.annotation(Indexes.class);
		if (idxes != null) {
			for (On index : idxes.value()) {
				map.put(index.property(), 
						createIndex(info, index));
			}
		}
		return map;
	}
	static IndexType createIndex(EntityInfo info, On index) {
		String name = index.name();
		if (name.isEmpty()) {
			name = info.getName() + "_" + index.property().toLowerCase();
		}
		List<String> gKey = info.groupKey();
		List<String> idxKey = fieldsFor(info, index);
		/**
		 * If index key contains the group key of the target,
		 * it is grouped with the target.
		 */
		boolean inGroup = new HashSet<String>(idxKey).containsAll(gKey);

		String gname = (inGroup ? info.getGroupName() : name);
		Class<?> idxKeyClass = info.getGetter(index.property())
				.getReturnType();
		KeyFinder gkFinder;
		if (inGroup && idxKey.size() > gKey.size()) {
			/**
			 * need to extract GK from index Key
			 */
			BeanInfo keyInfo = new BeanInfo(idxKeyClass);
			gkFinder = KeyFinder.finderFor(keyInfo,
					info.getGroupKeyClass(), gKey);
		} else {
			gkFinder = KeyFinder.identity(idxKeyClass);
		}
		return new IndexType(gname, name, info, gkFinder, index);
	}

	static List<String> fieldsFor(EntityInfo info, On index) {
		String property = index.property();
		List<String> list = new ArrayList<String>();
		if (info.findField(property) != null) {
			list.add(property);
			return list;
		}
		Method m = info.findGetter(property);
		if (m == null) {
			throw new RuntimeException("index property not accessible:"
					+ property);
		}
		BeanInfo keyInfo = new BeanInfo(m.getReturnType());
		for (Field f : keyInfo.fields()) {
			if (keyInfo.findGetter(f.getName()) != null) {
				list.add(f.getName());
			}
		}
		return list;
	}

	static KeySetter keySetterFor(EntityInfo info) {
		List<String> key = info.key();
		Class<?> keyClass = info.getKeyClass();
		BeanInfo keyInfo = new BeanInfo(keyClass);
		return KeySetter.createSetter(keyInfo, info, key);
	}
}
