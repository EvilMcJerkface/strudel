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
package com.nec.strudel.bench.micro.interactions;

import com.nec.strudel.bench.test.EntityBuilder;
import com.nec.strudel.entity.EntityUtil;

public class ContentBuilder<E> implements EntityBuilder<E> {
	private String prefix;
	private String name;
	private Object key;

	public ContentBuilder(String name, Object key) {
		this.name = name;
		this.key = key;
		this.prefix = "test:";
	}
	public ContentBuilder<E> prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	@Override
	public void build(E entity, int idx) {
		EntityUtil.setProperty(entity, name, key);
		EntityUtil.setProperty(entity, "content", prefix + idx);
	}

}
