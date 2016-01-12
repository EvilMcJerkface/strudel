/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
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
package com.nec.strudel.workload.test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;

public final class Resources {
    private Resources() {
        // not instantiated
    }

    public static <T> T getObject(String name, Class<T> cls) {
    	return getConfig(name).toObject(cls);
    }
    public static ConfigValue getConfig(String name) {
        URL url = getTestResource(name);
    	return Values.parseValue(url);
    }
    public static File getFile(String name) {
        URL url = getTestResource(name);
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static URL getTestResource(String name) {
        URL url = Resources.class.getResource("/" + name);
        if (url == null) {
            url = Resources.class.getResource("/" + name + ".xml");
        }
        return url;
    }
    public static <T> T create(ResourceFile<T> res) {
    	return getObject(res.file(), res.resourceClass());
    }
    public static File file(ResourceFile<?> res) {
    	return getFile(res.file());
    }

    public static <T> ResourceFile<T> of(final String file, final Class<T> cls) {
    	return new ResourceFile<T>() {

			@Override
			public String file() {
				return file;
			}

			@Override
			public Class<T> resourceClass() {
				return cls;
			}
		};
    }
}
