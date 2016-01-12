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
