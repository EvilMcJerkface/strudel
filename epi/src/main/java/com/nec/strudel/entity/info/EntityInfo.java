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

package com.nec.strudel.entity.info;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.IdClass;

import com.nec.strudel.entity.Group;
import com.nec.strudel.entity.GroupId;
import com.nec.strudel.entity.GroupIdClass;
import com.nec.strudel.entity.NameUtil;

public class EntityInfo extends BeanInfo {
    private final Class<?> entityClass;
    private String name;
    private String groupName;
    private Class<?> keyClass;
    private Class<?> groupKeyClass;
    private List<String> grpKeys;
    private List<String> keys;

    public EntityInfo(Class<?> entityClass) {
        super(entityClass);
        this.entityClass = entityClass;
    }

    public static String groupNameOf(Class<?> cls) {
        Group grp = cls.getAnnotation(Group.class);
        if (grp != null) {
            Class<?> parentClass = grp.parent();
            if (parentClass != Object.class) {
                return groupNameOf(parentClass);
            }
            String name = grp.group();
            if (name.isEmpty()) {
                name = grp.name();
            }
            if (!name.isEmpty()) {
                return name;
            }
        }
        return nameOf(cls);
    }

    public static String nameOf(Class<?> entityClass) {
        Group grp = entityClass.getAnnotation(Group.class);
        if (grp != null) {
            String name = grp.name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        return NameUtil.nameOf(entityClass);
    }

    public Class<?> entityClass() {
        return entityClass;
    }

    public String getName() {
        if (name == null) {
            name = nameOf(entityClass);
        }
        return name;
    }

    public String getGroupName() {
        if (groupName == null) {
            groupName = groupNameOf(entityClass);
        }
        return groupName;
    }

    public Class<?> getKeyClass() {
        if (keyClass == null) {
            keyClass = generateKeyClass();
        }
        return keyClass;
    }

    public Class<?> getGroupKeyClass() {
        if (groupKeyClass == null) {
            groupKeyClass = generateGroupKeyClass();
        }
        return groupKeyClass;
    }

    public List<String> key() {
        if (keys == null) {
            generateKeys();
        }
        return keys;
    }

    public List<String> groupKey() {
        if (grpKeys == null) {
            generateKeys();
        }
        return grpKeys;
    }

    void generateKeys() {
        List<String> ids = new ArrayList<String>();
        List<String> gids = new ArrayList<String>();
        for (Field f : entityClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                ids.add(f.getName());
            }
            if (f.isAnnotationPresent(GroupId.class)) {
                gids.add(f.getName());
            }
        }
        this.keys = ids;
        if (gids.isEmpty()) {
            this.grpKeys = ids;
        } else {
            this.grpKeys = gids;
        }
    }

    Class<?> generateGroupKeyClass() {
        /**
         * A compound group key must be annotated with GroupIdClass if it is
         * different from IdClass
         */
        GroupIdClass gidc = annotation(GroupIdClass.class);
        if (gidc != null) {
            return gidc.value();
        }
        List<String> ids = new ArrayList<String>();
        List<String> gids = new ArrayList<String>();
        List<Class<?>> gidClasses = new ArrayList<Class<?>>();
        for (Field f : entityClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                ids.add(f.getName());
            }
            if (f.isAnnotationPresent(GroupId.class)) {
                gids.add(f.getName());
                gidClasses.add(f.getType());
            }
        }
        Class<?> keyClass = getKeyClass();
        if (gids.isEmpty() || gids.size() == ids.size()) {
            return keyClass;
        } else if (gids.size() == 1) {
            return gidClasses.get(0);
        } else {
            throw new RuntimeException(
                    "@GroupIdClass not found for group key:"
                            + nameOf(entityClass)
                            + gids);
        }
    }

    Class<?> generateKeyClass() {
        /**
         * A compound primary key must be annotated with IdClass instead of
         * EmbeddedId
         */
        IdClass idc = annotation(IdClass.class);
        if (idc != null) {
            return idc.value();
        }

        /**
         * NOTE this assumes that there is only one field for Id.
         */
        for (Field f : entityClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                return f.getType();
            }
        }
        throw new RuntimeException("no @Id found");
    }
}