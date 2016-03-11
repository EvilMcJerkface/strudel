package com.nec.strudel.tkvs;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.nec.strudel.entity.IndexType;


public abstract class KeyArray {

    public static KeyArray create(IndexType type, byte[] image) {
        Class<?> targetKeyClass = type.targetKeyClass();
        if (Integer.TYPE.equals(targetKeyClass) || Integer.class.equals(targetKeyClass)) {
            return new IntKeyArray(image);
        }
        return new ByteKeyArray(Entities.getKeyConstructorOf(targetKeyClass),
                image);
    }

    public static KeyArray empty(IndexType type) {
        Class<?> targetKeyClass = type.targetKeyClass();
        if (Integer.TYPE.equals(targetKeyClass) || Integer.class.equals(targetKeyClass)) {
            return new IntKeyArray();
        }
        return new ByteKeyArray(Entities.getKeyConstructorOf(targetKeyClass));
    }

    public abstract int size();


    public abstract Key getKey(int index);

    public abstract byte[] toBytes();

    public abstract KeyArray insert(Key ref);

    public abstract KeyArray remove(Key key);

    public static class IntKeyArray extends KeyArray {
        private static final int INT_SIZE = 4;
        private final int[] values;

        public IntKeyArray(int... values) {
            this.values = values;
        }

        public IntKeyArray(byte[] image) {
            int size = image.length / INT_SIZE;
            this.values = new int[size];
            ByteBuffer buff = ByteBuffer.wrap(image);
            for (int i = 0; i < size; i++) {
                values[i] = buff.getInt();
            }
        }

        @Override
        public int size() {
            return values.length;
        }

        @Override
        public Key getKey(int index) {
            return Key.create(values[index]);
        }

        @Override
        public byte[] toBytes() {
            byte[] data = new byte[INT_SIZE * size()];
            ByteBuffer buff = ByteBuffer.wrap(data);
            for (int v : values) {
                buff.putInt(v);
            }
            return data;
        }

        @Override
        public IntKeyArray insert(Key ref) {
            int val = ref.convert(Integer.class);
            for (int v : values) {
                if (val == v) {
                    return this;
                }
            }
            int[] newVals = Arrays.copyOf(
                    values, values.length + 1);
            newVals[values.length] = val;
            return new IntKeyArray(newVals);
        }

        @Override
        public KeyArray remove(Key key) {
            int val = key.convert(Integer.class);
            for (int i = 0; i < values.length; i++) {
                if (val == values[i]) {
                    return removeAt(i);
                }
            }
            return this;
        }

        private IntKeyArray removeAt(int idx) {
            int[] vals = new int[values.length - 1];
            for (int i = 0; i < idx; i++) {
                vals[i] = values[i];
            }
            for (int i = idx; i < vals.length; i++) {
                vals[i] = values[i + 1];
            }
            return new IntKeyArray(vals);
        }
    }

    public static class ByteKeyArray extends KeyArray {
        private final KeyConstructor cons;
        private final byte[][] values;

        public ByteKeyArray(KeyConstructor cons, byte[] image) {
            this.cons = cons;
            this.values = VarArrayFormat.toByteArray(image);
        }

        public ByteKeyArray(KeyConstructor cons, byte[][] values) {
            this.cons = cons;
            this.values = values;
        }

        public ByteKeyArray(KeyConstructor cons) {
            this.cons = cons;
            this.values = new byte[0][];
        }

        @Override
        public int size() {
            return values.length;
        }


        @Override
        public Key getKey(int index) {
            return cons.read(values[index]);
        }

        @Override
        public byte[] toBytes() {
            return VarArrayFormat.toBytes(values);
        }

        @Override
        public ByteKeyArray insert(Key ref) {
            byte[] value = cons.toBytes(ref);
            for (byte[] v : values) {
                if (Arrays.equals(value, v)) {
                    return this;
                }
            }
            byte[][] newVals = Arrays.copyOf(
                    values, values.length + 1);
            newVals[values.length] = value;
            return new ByteKeyArray(cons, newVals);
        }

        @Override
        public ByteKeyArray remove(Key key) {
            byte[] value = cons.toBytes(key);
            for (int i = 0; i < values.length; i++) {
                if (Arrays.equals(value, values[i])) {
                    return removeAt(i);
                }
            }
            return this;
        }

        private ByteKeyArray removeAt(int idx) {
            byte[][] vals = new byte[values.length - 1][];
            for (int i = 0; i < idx; i++) {
                vals[i] = values[i];
            }
            for (int i = idx; i < vals.length; i++) {
                vals[i] = values[i + 1];
            }
            return new ByteKeyArray(cons, vals);
        }

    }
}
