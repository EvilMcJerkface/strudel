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
package com.nec.strudel.bench.test.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;

import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamName;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class Executor<T> {
	private final Interaction<T> intr;
	private final T db;
	private final Param param = new Param();
	private Result res;
	public Executor(Interaction<T> intr, T db) {
		this.intr = intr;
		this.db = db;
	}
	public Executor<T> param(ParamName name, Object value) {
		param.put(name, value);
		return this;
	}
	public Executor<T> execute() {
		res = exec();
		return this;
	}
	public Executor<T> success() {
		assertTrue(res.isSuccess());
		return this;
	}
	public Executor<T> success(String mode) {
		assertTrue(res.isSuccess());
		Assert.assertEquals(mode, res.getMode());
		return this;
	}
	public Executor<T> equals(ParamName name, Object value) {
		assertEquals(res.get(name), value);
		return this;
	}
	public Executor<T> sameEntity(ParamName name, Object value) {
		EntityAssert.assertEntityEquals(value, res.get(name));
		return this;
	}
	public Executor<T> isNull(ParamName name) {
		Assert.assertNull(res.get(name));
		return this;
	}
	public Executor<T> emptyList(ParamName name) {
		List<?> list = res.get(name);
		Assert.assertTrue(list.isEmpty());
		return this;
	}
	public <E> Executor<T> entitiySet(ParamName name, List<E> entities) {
		List<E> list = res.get(name);
		EntityAssert.assertSameEntitySets(entities, list);
		return this;
	}
	public Result executeSuccess() {
		res = exec();
		assertTrue(res.isSuccess());
		return res;
	}
	public Result executeSuccess(String mode) {
		res = exec();
		assertTrue(res.isSuccess());
		assertEquals(mode, res.getMode());
		return res;
	}
	public Result executeFailure(String mode) {
		res = exec();
		assertFalse(res.isSuccess());
		assertEquals(mode, res.getMode());
		return res;
	}
	public Result result() {
		return res;
	}
	public Param getParam() {
		return param;
	}
	private Result exec() {
		return intr.execute(param, db, new ResultBuilder());
	}

}