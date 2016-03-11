/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.nec.strudel.tkvs;

import java.util.List;

import com.nec.strudel.entity.info.BeanInfo;

public class EntityConstructor {
    private final BeanInfo info;
    private final RecordFormat format;

    public EntityConstructor(BeanInfo info, RecordFormat format) {
        this.info = info;
        this.format = format;
    }

    public static EntityConstructor of(Class<?> entityClass) {
        BeanInfo info = new BeanInfo(entityClass);
        RecordFormat format = formatFor(info);
        return new EntityConstructor(info, format);
    }

    public static RecordFormat formatFor(BeanInfo info) {
        List<Class<?>> list = info.types();
        Class<?>[] types = list.toArray(new Class<?>[list.size()]);
        /*
         * TODO use more efficient format
         */
        return new ArrayRecordFormat(types);
    }

    public Record toRecord(Object entity) {
        Object[] values = info.toTuple(entity);
        return SimpleRecord.create(
                format.serialize(values));
    }

    public <T> T create(Record record) {
        @SuppressWarnings("unchecked")
        T entity = (T) info.create(
                format.deserialize(record.toBytes()));
        return entity;
    }


}
