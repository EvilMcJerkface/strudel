package com.nec.strudel.util;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ClassUtil {
	public static final String PARAM_CLASSPATH_BASE = "strudel.classpath.base";
	/**
	 * Cache ClassLoader of the same path.
	 */
	private static final Map<String, ClassLoader> LOADERS =
			new HashMap<String, ClassLoader>();
	private ClassUtil() {
		// not instantiated
	}

	/**
	 * Create an instance of the specified class in
	 * the specified class paths. 
	 * @param className
	 * @param classPaths colon-separated paths
	 * @return an instance of the class
	 */
	@SuppressWarnings("unchecked")
	public static <T> T create(String className, String classPaths) {
		if (classPaths.isEmpty()) {
			return create(className);
		}
		ClassLoader loader = loaderFor(classPaths);
		try {
			Class<?> cls = loader.loadClass(className);
			return (T) create(cls);
		} catch (ClassNotFoundException e) {
			throw new ClassException(
				"class not found: " + className, e);
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T create(String className, URL... urls) {
		try {
			Class<?> cls = getLoader(urls).loadClass(className);
			return (T) create(cls);
		} catch (ClassNotFoundException e) {
			throw new ClassException(
				"class not found: " + className, e);
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T create(String className, ClassLoader loader) {
		try {
			Class<?> cls = loader.loadClass(className);
			return (T) create(cls);
		} catch (ClassNotFoundException e) {
			throw new ClassException(
				"class not found: " + className, e);
		}
		
	}
	static ClassLoader getLoader(URL[] urls) {
		if (urls.length > 0) {
			return new URLClassLoader(urls,
					ClassUtil.class.getClassLoader());
		} else {
			return ClassUtil.class.getClassLoader();
		}
	}
	public static <T> T create(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
			throw new ClassException(
				"cannot instantiate: " + cls, e);
		} catch (IllegalAccessException e) {
			throw new ClassException(
				"cannot access: " + cls, e);
		}

	}
	public static Class<?> forName(String className, String classPath) {
		try {
			if (classPath.isEmpty()) {
				return Class.forName(className);
			}
			return loaderFor(classPath).loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ClassException(
					"class not found: " + className, e);
		}
	}
	public static Class<?> forName(String className, ClassLoader loader) {
		try {
			return loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ClassException(
					"class not found: " + className, e);
		}
	}
	public static boolean isSubclass(Class<?> thisClass,
			Class<?> superClass) {
		try {
			thisClass.asSubclass(superClass);
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}
	public static ClassLoader loaderFor(String classPath) {
		if (classPath.isEmpty()) {
			return ClassUtil.class.getClassLoader();
		}
		synchronized (LOADERS) {
			ClassLoader loader = LOADERS.get(classPath);
			if (loader == null) {
				loader = createLoader(classPath);
				LOADERS.put(classPath, loader);
			}
			return loader;
		}
	}
	private static ClassLoader createLoader(String classPath) {
		List<URL> urls = new ArrayList<URL>();
		for (String path : classPath.split(":")) {
			path = path.trim();
			if (path.endsWith("/*")) {
				File libDir = toFile(path.substring(0, path.indexOf("/*")));
				if (!libDir.isDirectory()) {
					throw new ClassException(
					"invalid class path: " + path
					+ " - no directory: " + libDir.getAbsolutePath());
				}
				for (File f : libDir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.isFile();
					}
				})) {
					addFile(f, urls);
				}

			} else if (!path.isEmpty()) {
				addFile(toFile(path), urls);
			}
		}
		return getLoader(urls.toArray(new URL[urls.size()]));
	}
	private static File toFile(String path) {
		String baseDir = System.getProperty(PARAM_CLASSPATH_BASE);
		if (baseDir != null) {
			return new File(baseDir, path);
		}
		return new File(path);
	}
	private static void addFile(File f, List<URL> urls) {
		try {
			urls.add(f.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new ClassException("malformed url", e);
		}

	}
	public static class ClassException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public ClassException(String msg, Throwable cause) {
			super(msg, cause);
		}
		public ClassException(String msg) {
			super(msg);
		}
	}
}
