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
