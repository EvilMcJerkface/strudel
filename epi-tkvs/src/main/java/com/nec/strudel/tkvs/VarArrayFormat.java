package com.nec.strudel.tkvs;

import java.nio.ByteBuffer;

public final class VarArrayFormat {
    private static final int INT_SIZE = 4;

    private VarArrayFormat() {
    }

    public static String[] toStringArray(byte[] image) {
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

    public static byte[][] toByteArray(byte[] image) {
        ByteBuffer buff = ByteBuffer.wrap(image);
        int size = buff.getInt();
        int[] lens = new int[size];
        byte[][] values = new byte[size][];
        for (int i = 0; i < size; i++) {
            lens[i] = buff.getInt();
        }
        for (int i = 0; i < size; i++) {
            byte[] value = new byte[lens[i]];
            buff.get(value);
            values[i] = value;
        }
        return values;
    }

    /**
     * Serializes an array of String values (where
     * the size of array and size of strings are
     * variable).
     * @param values an array of String.
     * @return a byte array.
     */
    public static byte[] toBytes(String[] values) {
        int size = values.length;
        int[] lens = new int[size];
        /*
         * NOTE it has an additional int value
         * for the size (the number of values)
         * at the beginning.
         */
        int byteSize = INT_SIZE
                + size * INT_SIZE;
        byte[][] vals = new byte[size][];
        for (int i = 0; i < size; i++) {
            byte[] val = values[i].getBytes();
            lens[i] = val.length;
            vals[i] = val;
            byteSize += val.length;
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

    /**
     * Serializes an array of byte arrays (with variable length).
     * @param values an array of byte arrays.
     * @return a byte array that contains the serialized data.
     */
    public static byte[] toBytes(byte[][] values) {
        int size = values.length;
        int[] lens = new int[size];
        /*
         * NOTE it has an additional int value
         * for the size (the number of values)
         * at the beginning.
         */
        int byteSize = INT_SIZE
                + size * INT_SIZE;
        byte[][] vals = new byte[size][];
        for (int i = 0; i < size; i++) {
            byte[] val = values[i];
            lens[i] = val.length;
            vals[i] = val;
            byteSize += val.length;
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
