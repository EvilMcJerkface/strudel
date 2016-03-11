package com.nec.strudel.tkvs;

import java.nio.ByteBuffer;

/**
 * A formatter that serializes all the elements of a record
 * as variable-length byte arrays.
 * @author tatemura
 *
 */
public class ArrayRecordFormat implements RecordFormat {
    private static final int INT_SIZE = 4;

    private final int size;
    private final TypeUtil.TypeConv<?>[] convs;

    public ArrayRecordFormat(Class<?>[] types) {
        this.size = types.length;
        this.convs = new TypeUtil.TypeConv<?>[size];
        for (int i = 0; i < size; i++) {
            convs[i] = TypeUtil.converterOf(types[i]);
        }
    }

    public byte[] serialize(Object[] tuple) {
        if (size != tuple.length) {
            throw new TkvStoreException("invalid size of tuple");
        }
        int[] lens = new int[size];
        int byteSize = size * INT_SIZE;
        byte[][] vals = new byte[size][];
        for (int i = 0; i < size; i++) {
            byte[] val = convs[i].toBytes(tuple[i]);
            lens[i] = val.length;
            vals[i] = val;
            byteSize += val.length;
        }
        ByteBuffer buff = ByteBuffer.allocate(byteSize);
        for (int len : lens) {
            buff.putInt(len);
        }
        for (byte[] v : vals) {
            buff.put(v);
        }
        return buff.array();
    }

    public Object[] deserialize(byte[] image) {
        ByteBuffer buff = ByteBuffer.wrap(image);
        int[] lens = new int[size];
        Object[] values = new Object[size];
        for (int i = 0; i < size; i++) {
            lens[i] = buff.getInt();
        }
        for (int i = 0; i < size; i++) {
            byte[] value = new byte[lens[i]];
            buff.get(value);
            values[i] = convs[i].fromBytes(value);
        }
        return values;
    }

}
