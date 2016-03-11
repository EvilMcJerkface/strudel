package com.nec.strudel.tkvs;

import java.util.Arrays;

import com.nec.strudel.entity.IndexType;


public class KeyArray {

    private final byte[][] values;

    public KeyArray(byte[]... values) {
        this.values = values;
    }

    public static KeyArray create(IndexType type, byte[] image) {
        return new KeyArray(VarArrayFormat.toByteArray(image));
    }

    public static KeyArray empty(IndexType type) {
        return new KeyArray();
    }

    public int size() {
        return values.length;
    }


    public Key getKey(int index) {
        return Key.parse(values[index]);
    }

    public byte[] toBytes() {
        return VarArrayFormat.toBytes(values);
    }

    public KeyArray insert(Key ref) {
        byte[] value = ref.toByteKey();
        for (byte[] v : values) {
            if (Arrays.equals(value, v)) {
                return this;
            }
        }
        byte[][] newVals = Arrays.copyOf(
                values, values.length + 1);
        newVals[values.length] = value;
        return new KeyArray(newVals);
    }


    public KeyArray remove(Key key) {
        byte[] value = key.toByteKey();
        for (int i = 0; i < values.length; i++) {
            if (Arrays.equals(value, values[i])) {
                return removeAt(i);
            }
        }
        return this;
    }

    private KeyArray removeAt(int idx) {
        byte[][] vals = new byte[values.length - 1][];
        for (int i = 0; i < idx; i++) {
            vals[i] = values[i];
        }
        for (int i = idx; i < vals.length; i++) {
            vals[i] = values[i + 1];
        }
        return new KeyArray(vals);
    }

}
