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