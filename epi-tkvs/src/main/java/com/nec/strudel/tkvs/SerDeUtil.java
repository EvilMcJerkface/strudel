package com.nec.strudel.tkvs;

import java.nio.ByteBuffer;

public final class SerDeUtil {
	private SerDeUtil() {
		// not instantiated
	}
	public static Record parseRecord(byte[] image) {
		return Record.create(parse(image));
	}
	/**
	 * @deprecated user Key.parse(byte[] image);
	 * @param image
	 * @return
	 */
	public static Key parseKey(byte[] image) {
		return Key.parse(image);
	}
	public static byte[] toBytes(String name) {
		return name.getBytes();
	}
	/**
	 * @deprecated use key.toBytes()
	 * @param key
	 * @return
	 */
	public static byte[] toBytes(Key key) {
		return key.toBytes();
	}
	public static byte[] toBytes(Record record) {
		return toBytes(record.getValues());
	}
	public static String[] parse(byte[] image) {
		ByteBuffer buff = ByteBuffer.wrap(image);
		int size = buff.getInt();
		int[] lens = new int[size];
		String[] values = new String[size];
		for (int i = 0; i < size; i++) {
			lens[i] = buff.getInt();
		}
		for (int i = 0; i < size; i++) {
			byte[] value = new byte[lens[i]];
			buff.get(value);
			values[i] = new String(value);
		}
		return values;
	}
	private static final int INT_SIZE = 4;
	public static byte[] toBytes(String[] values) {
		int size = values.length;
		int[] lens = new int[size];
		int byteSize = INT_SIZE
			+ size * INT_SIZE;
		byte[][] vals = new byte[size][];
		for (int i = 0; i < size; i++) {
			byte[] v = values[i].getBytes();
			lens[i] = v.length;
			vals[i] = v;
			byteSize += v.length;
		}
		ByteBuffer buff = ByteBuffer.allocate(byteSize);
		buff.putInt(size);
		for (int len : lens) {
			buff.putInt(len);
		}
		for (byte[] v : vals) {
			buff.put(v);
		}
		return buff.array();
	}

}
