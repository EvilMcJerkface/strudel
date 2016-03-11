package com.nec.strudel.tkvs;

import java.util.Arrays;

public class SimpleRecord implements Record {
    private final byte[] value;

    public SimpleRecord(byte[] image) {
        this.value = image;
    }

    public static SimpleRecord create(byte[] image) {
        return new SimpleRecord(image);
    }

    public byte[] toBytes() {
        return value;
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SimpleRecord) {
            SimpleRecord record = (SimpleRecord) obj;
            return Arrays.equals(value, record.value);
        }
        return false;
    }


}