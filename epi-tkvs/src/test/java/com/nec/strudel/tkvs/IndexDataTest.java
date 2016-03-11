package com.nec.strudel.tkvs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;

import org.junit.Test;

import com.nec.strudel.entity.IndexType;
import com.nec.strudel.entity.On;

public class IndexDataTest {

    @Test
    public void testSingleIntKey() {
        IndexType type = IndexType.on(TestEntity.class, "test");
        Key idxKey = Key.create(0);
        IndexData data = IndexData.create(type, idxKey, idxKey);
        assertTrue(data.isEmpty());
        data.insert(Key.create(1));
        assertEquals(1, data.size());
        data.insert(Key.create(2));
        assertEquals(2, data.size());
        data.insert(Key.create(1));
        assertEquals(2, data.size());

        IndexData data1 = IndexData.create(
                type, idxKey, idxKey, data.toRecord());

        Set<Integer> ids = new HashSet<Integer>(); 
        for (Integer id : data1.scan(Integer.class)) {
            assertFalse(ids.contains(id));
            ids.add(id);
        }
        assertEquals(data.size(), ids.size());
    }

    @com.nec.strudel.entity.Indexes({
        @On(property = "test")
    })
    public static class TestEntity {
        @Id
        private int id;
        private int test;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getTest() {
            return test;
        }
        public void setTest(int test) {
            this.test = test;
        }
        
    }
}
