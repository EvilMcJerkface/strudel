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

package com.nec.strudel.entity.jpa;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.nec.strudel.entity.EntityUtil;
import com.nec.strudel.entity.NameUtil;
import com.nec.strudel.entity.info.BeanInfo;
import com.nec.strudel.entity.info.EntityInfo;

public final class IndexQuery {
    private final EntityManager em;
    private final Map<IndexKey, QueryExec> getExecs = new HashMap<IndexKey, QueryExec>();

    public IndexQuery(EntityManager em) {
        this.em = em;
    }

    private QueryExec scanId(Class<?> ec,
            String property) {
        throw new RuntimeException("TODO implement: scanId"); // TODO implement
    }

    private QueryExec getByIndex(Class<?> ec,
            String property) {
        IndexKey key = new IndexKey(ec, property);
        QueryExec exec = getExecs.get(key);
        if (exec == null) {
            exec = createGetByIndex(ec, property);
            getExecs.put(key, exec);
        }
        return exec;
    }

    private QueryExec createGetByIndex(Class<?> ec,
            String property) {
        String name = NameUtil.localNameOf(ec);
        StringBuilder builder = new StringBuilder()
                .append("SELECT e FROM ")
                .append(name)
                .append(" e WHERE ");
        if (hasProperty(ec, property)) {
            builder.append("e." + property + " = ?1");
            return new SinglePropQueryExec(builder.toString());
        } else {
            EntityInfo info = new EntityInfo(ec);
            BeanInfo prop = new BeanInfo(propertyClass(info, property));
            List<String> list = prop.properties();
            String[] props = list.toArray(new String[list.size()]);

            for (int i = 0; i < props.length; i++) {
                if (i > 0) {
                    builder.append(" AND ");
                }
                builder.append("e.").append(props[i])
                        .append(" = ?").append(i + 1);
            }
            return new CompoundQueryExec(builder.toString(), props);
        }
    }

    static Class<?> propertyClass(EntityInfo info, String property) {
        Method method = info.findGetter(property);
        if (method == null) {
            throw new RuntimeException("unknown property '" + property
                    + "' on " + info.getName());
        }
        return method.getReturnType();
    }

    public static class IndexKey {
        private final Class<?> entityClass;
        private final String property;
        private final int hashCode;

        public IndexKey(Class<?> entityClass,
                String property) {
            this.entityClass = entityClass;
            this.property = property;
            this.hashCode = entityClass.hashCode()
                    + property.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof IndexKey) {
                IndexKey key = (IndexKey) obj;
                return this.entityClass.equals(key.entityClass)
                        && this.property.equals(key.property);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    public abstract static class QueryExec {
        private final String query;
        private final String[] params;

        public QueryExec(String query, String... params) {
            this.query = query;
            this.params = params;
        }

        public String queryString() {
            return query;
        }

        public String[] params() {
            return params;
        }

        public abstract <T> List<T> exec(EntityManager em,
                Class<T> valueClass, Object key);
    }

    public static class CompoundQueryExec extends QueryExec {
        public CompoundQueryExec(String query, String... params) {
            super(query, params);
        }

        public <T> List<T> exec(EntityManager em,
                Class<T> valueClass, Object key) {
            TypedQuery<T> query = em.createQuery(
                    queryString(), valueClass);
            String[] params = params();
            if (params.length == 0) {
                query.setParameter(1, key);
            } else {
                for (int i = 1; i <= params.length; i++) {
                    query.setParameter(i,
                            EntityUtil.getProperty(key, params[i - 1]));
                }
            }
            return query.getResultList();
        }
    }

    public static class SinglePropQueryExec extends QueryExec {
        public SinglePropQueryExec(String query) {
            super(query);
        }

        public <T> List<T> exec(EntityManager em,
                Class<T> valueClass, Object key) {
            TypedQuery<T> query = em.createQuery(
                    queryString(), valueClass)
                    .setParameter(1, key);
            return query.getResultList();
        }
    }

    public <T> List<T> queryByIndex(Class<T> ec, String property, Object key) {
        return getByIndex(ec, property).exec(em, ec, key);
    }

    public <T> List<T> scanIdByIndex(Class<T> idClass, Class<?> ec,
            String property, Object key) {
        return scanId(ec, property).exec(em, idClass, key);
    }

    static boolean hasProperty(Class<?> entityClass, String name) {
        try {
            entityClass.getDeclaredField(name);
            return true;
        } catch (NoSuchFieldException ex) {
            return false;
        } catch (SecurityException ex) {
            return false;
        }
    }
}
