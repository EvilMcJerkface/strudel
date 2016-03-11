package com.nec.strudel.tkvs;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class TypeUtil {
    private static final int INT_SIZE = 4;
    private static final int LONG_SIZE = 8;
    private static final int DOUBLE_SIZE = 8;

    private static final Map<Class<?>, TypeConv<?>> CONVS =
            new HashMap<Class<?>, TypeConv<?>>();

    static {
        CONVS.put(Integer.class, new IntConv());
        CONVS.put(Integer.TYPE, new IntConv());
        CONVS.put(Long.class,  new LongConv());
        CONVS.put(Long.TYPE,  new LongConv());
        CONVS.put(Double.class, new DoubleConv());
        CONVS.put(Double.TYPE, new DoubleConv());
        CONVS.put(String.class, new StringConv());
    }

    private TypeUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertType(Object value, Class<T> dstType) {
        if (dstType.isInstance(value)) {
            return dstType.cast(value);
        }
        return (T) converterOf(dstType).convert(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromBytes(byte[] val, Class<T> type) {
        return (T) converterOf(type).fromBytes(val);
    }

    public static byte[] toBytes(Object val, Class<?> type) {
        return converterOf(type).toBytes(val);
    }

    public static TypeConv<?> converterOf(Class<?> type) {
        TypeConv<?> conv = CONVS.get(type);
        if (conv == null) {
            throw new RuntimeException(
                    "unknown type: " + type);
        }
        return conv;
    }

    public interface TypeConv<T> {
        T convert(Object val);

        byte[] toBytes(Object val);

        T fromBytes(byte[] val);
    }

    public static class IntConv implements TypeConv<Integer> {

        @Override
        public Integer convert(Object val) {
            if (val instanceof Integer) {
                return (Integer) val;
            }
            return Integer.valueOf(val.toString());
        }

        @Override
        public byte[] toBytes(Object val) {
            Integer intValue = convert(val);
            byte[] value = new byte[INT_SIZE];
            ByteBuffer.wrap(value).putInt(intValue);
            return value;
        }

        @Override
        public Integer fromBytes(byte[] val) {
            return ByteBuffer.wrap(val).getInt();
        }
    }

    public static class LongConv implements TypeConv<Long> {

        @Override
        public Long convert(Object val) {
            if (val instanceof Long) {
                return (Long) val;
            }
            if (val instanceof Integer) {
                return Long.valueOf(
                      ((Integer) val).intValue());
            }
            return Long.valueOf(val.toString());
        }

        @Override
        public byte[] toBytes(Object val) {
            byte[] value = new byte[LONG_SIZE];
            ByteBuffer.wrap(value).putLong(convert(val));
            return value;
        }

        @Override
        public Long fromBytes(byte[] val) {
            return ByteBuffer.wrap(val).getLong();
        }
    }

    public static class DoubleConv implements TypeConv<Double> {

        @Override
        public Double convert(Object val) {
            if (val instanceof Double) {
                return (Double) val;
            } else if (val instanceof Number) {
                return ((Number) val).doubleValue();
            }
            return Double.valueOf(val.toString());
        }

        @Override
        public byte[] toBytes(Object val) {
            byte[] value = new byte[DOUBLE_SIZE];
            ByteBuffer.wrap(value).putDouble(convert(val));
            return value;
        }

        @Override
        public Double fromBytes(byte[] val) {
            return ByteBuffer.wrap(val).getDouble();
        }
        
    }

    public static class StringConv implements TypeConv<String> {

        @Override
        public String convert(Object val) {
            return val.toString();
        }

        @Override
        public byte[] toBytes(Object val) {
            return convert(val).getBytes();
        }

        @Override
        public String fromBytes(byte[] val) {
            return new String(val);
        }
        
    }
}
