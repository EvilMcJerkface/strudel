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
package com.nec.strudel.bench.micro.populate;

import java.util.Random;

import com.nec.strudel.util.RandomSelector;

public class ContentSet {
	public static ContentSet create(int groupId, int size,
			RandomSelector<String> selector, Random r) {
		String[] contents = new String[size];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = selector.next(r);
		}
		return new ContentSet(groupId, contents);

	}
	private final int groupId;
	private final String[] contents;
	public ContentSet(int groupId, String[] contents) {
		this.groupId = groupId;
		this.contents = contents;
	}

	public int getGroupId() {
		return groupId;
	}
	public String[] getContents() {
		return contents;
	}
}