package com.nec.strudel.tkvs;

public interface RecordFormat {

    byte[] serialize(Object[] tuple);

    Object[] deserialize(byte[] image);
}
