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

package com.nec.strudel.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Table;

public final class NameUtil {
    private static final Pattern NAME_PART = Pattern.compile(".*?(\\w+)\\z");

    private NameUtil() {
    }

    public static String nameOf(Class<?> entityClass) {
        Table tab = entityClass.getAnnotation(Table.class);
        if (tab != null) {
            String name = tab.name();
            if (name != null && !name.isEmpty()) {
                return name.toLowerCase();
            }
        }
        return convert(getLocalName(entityClass));
    }

    public static String localNameOf(Class<?> entityClass) {
        return getLocalName(entityClass);
    }

    private static String convert(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    private static String getLocalName(Class<?> cls) {
        Matcher match = NAME_PART.matcher(cls.getName());
        if (match.matches()) {
            return match.group(1);
        } else {
            return null;
        }
    }
}
