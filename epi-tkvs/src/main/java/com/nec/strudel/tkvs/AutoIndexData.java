package com.nec.strudel.tkvs;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.nec.strudel.entity.IndexType;

/**
 * Index data that is used to implement auto-increment IDs.
 * @author tatemura
 *
 */
public class AutoIndexData extends IndexData {
    private AutoArray value;

    protected AutoIndexData(IndexType type, Key groupKey, Key key,
            AutoArray value) {
        super(type, groupKey, key);
        this.value = value;
    }

    /**
     * Creates a data item from a serialized image.
     * @param type the index type.
     * @param groupKey transaction group key
     * @param key the key of the index
     * @param data a serialized image of the index data.
     */
    protected AutoIndexData(IndexType type, Key groupKey, Key key,
            byte[] data) {
        super(type, groupKey, key);
        this.value = AutoArray.create(data);
    }

    /**
     * Creates an empty data item.
     * @param type the index type.
     * @param groupKey transaction group key
     * @param key the key of the index
     */
    public AutoIndexData(IndexType type, Key groupKey, Key key) {
        super(type, groupKey, key);
        this.value = AutoArray.empty();
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isEmpty() {
        return value.size() == 0;
    }

    @Override
    public void insert(Key ref) {
        throw new RuntimeException(
                "cannot insert to auto-increment index:"
                        + this.getName());
    }

    @Override
    public void remove(Key ref) {
        throw new RuntimeException(
                "cannot remove from auto-increment index:"
                        + getName());
    }

    @SuppressWarnings("unchecked")
    public <T> T getTargetKey(int index) {
        return (T) toTargetKey(
                value.getKey(index));
    }

    private Object toTargetKey(Key entry) {
        return constructor().createKey(
                getKey().concat(entry));
    }

    @Override
    public Object createNewKey() {
        int entry = value.nextInt();
        value = value.insertIntId(entry);
        return toTargetKey(
                Key.create(entry));
    }

    @Override
    public byte[] toBytes() {
        return value.toBytes();
    }

    @Override
    protected int contentHashCode() {
        return value.hashCode();
    }

    @Override
    protected boolean contentEquals(IndexData data) {
        if (data instanceof AutoIndexData) {
            value.equals(((AutoIndexData) data).value);
        }
        return false;
    }

    public static class AutoArray {
        public static AutoArray create(byte[] image) {
            int size = image.length / INT_SIZE;
            int[] values = new int[size];
            ByteBuffer buff = ByteBuffer.wrap(image);
            for (int i = 0; i < size; i++) {
                values[i] = buff.getInt();
            }
            return new AutoArray(values);
        }
    
        public static AutoArray empty() {
            return new AutoArray();
        }
    
        private final int[] values;
    
        public AutoArray(int... values) {
            this.values = values;
        }
    
        public int size() {
            return values.length;
        }
    
    
        public Key getKey(int index) {
            return Key.create(values[index]);
        }
    
        private static final int INT_SIZE = 4;
    
        public byte[] toBytes() {
            int len = INT_SIZE * values.length;
            ByteBuffer buff = ByteBuffer.allocate(len);
            for (int v : values) {
                buff.putInt(v);
            }
            return buff.array();
        }
    
        protected AutoArray append(int... additionals) {
            int[] newVals = Arrays.copyOf(
                    values, values.length + additionals.length);
            for (int i = 0; i < additionals.length; i++) {
                newVals[i + values.length] = additionals[i];
            }
            return new AutoArray(newVals);
        }
    
        public AutoArray insertIntId(int ref) {
            for (int s : values) {
                if (s == ref) {
                    return this;
                }
            }
            return append(ref);
        }
    
        public int nextInt() {
            if (values.length == 0) {
                return 1;
            } else {
                /**
                 * NOTE assumes that auto increment int are inserted in order.
                 */
                return values[values.length - 1] + 1;
            }
        }
    
        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }
    
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof AutoArray) {
                AutoArray arry = (AutoArray) obj;
                return Arrays.equals(this.values, arry.values);
            }
            return false;
        }
    }

}
