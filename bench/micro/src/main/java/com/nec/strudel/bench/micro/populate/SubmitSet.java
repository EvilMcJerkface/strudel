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

public class SubmitSet {
	public static SubmitSet create(int sender, int size,
			RandomSelector<Integer> receiver,
			RandomSelector<String> content, Random rand) {
		int[] receivers = new int[size];
		String[] contents = new String[size];
		for (int i = 0; i < size; i++) {
			receivers[i] = receiver.next(rand);
			contents[i] = content.next(rand);
		}
		return new SubmitSet(sender, receivers, contents);
	}

	private final int sender;
	private final int[] receivers;
	private final String[] contents;
	public SubmitSet(int sender, int[] receivers, String[] contents) {
		this.sender = sender;
		this.receivers = receivers;
		this.contents = contents;
	}
	public int size() {
		return receivers.length;
	}
	public int getSender() {
		return sender;
	}
	public int getReceiver(int idx) {
		return receivers[idx];
	}
	public String getContent(int idx) {
		return contents[idx];
	}
}
