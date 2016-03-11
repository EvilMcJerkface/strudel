package com.nec.strudel.tkvs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EntityConstructorTest {

    @Test
    public void test() {
        EntityConstructor cons = EntityConstructor.of(TestEntity.class);
        TestEntity entity = new TestEntity();
        entity.setId(1);
        entity.setNum(10);
        entity.setTest("tttt");
        entity.setLongVal(Long.MAX_VALUE - 1);
        entity.setValue(-1);
        Record record = cons.toRecord(entity);
        TestEntity entity1 = cons.create(record);
        assertEquals(entity.getId(), entity1.getId());
        assertEquals(entity.getTest(), entity1.getTest());
    }

    public static class TestEntity {
        private int id;
        private int num;
        private long longVal;
        private String test;
        private double value;

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getNum() {
            return num;
        }
        public void setNum(int num) {
            this.num = num;
        }
        public String getTest() {
            return test;
        }
        public void setTest(String test) {
            this.test = test;
        }
        public double getValue() {
            return value;
        }
        public void setValue(double value) {
            this.value = value;
        }
        public long getLongVal() {
            return longVal;
        }
        public void setLongVal(long longVal) {
            this.longVal = longVal;
        }
    }
}
