package com.nec.strudel.management.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The information of a resource consists of
 * <ul>
 * <li> domain: the domain of this resource type.
 * <li> type: the type of resource. The pair of (domain,
 * type) should uniquely identify the class (type) of
 * this resource.
 * <li> name: the name of resource, which can be assigned
 * to individual instances of this resource type.
 * <li> id: the id of resource, which can be assigned
 * to individual instances of this resource type. When
 * it is used, a tuple (domain, type, name, id) should uniquely
 * identify the instance of this resource.
 * <li> description: the description of this
 * resource (type).
 * <li> attributes (ResourceAttribute): A set
 * of named attributes with access methods (getters and/or setters).
 * </ul>
 * @author tatemura
 *
 */
public class ResourceInfo {
	private String domain;
	private String type;
	private String name;
	private String id;
	private String description;
	private ResourceAttribute[] attributes = new ResourceAttribute[0];
	public ResourceInfo() {
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	public ResourceAttribute[] getAttributes() {
		return attributes;
	}
	public void setAttributes(ResourceAttribute... attributes) {
		this.attributes = attributes;
	}
	/**
	 * <ul>
	 * <li> domain name: if it is specified in
	 * the managed object annotation use it. Otherwise,
	 * use the package name of this object.
	 * <li> type: use the specified one if it is in the
	 * ManagedObject annotation. Otherwise, use the class
	 * name of this object.
	 * <li> name: use the result of a method call if the
	 * ResourceName annotation is specified. Otherwise, omit
	 * this property.
	 * <li> id: use the result of a method call if the
	 * ResourceId annotation is specified. Otherwise, omit
	 * this property.
	 * </ul>
	 * @param resource object
	 * @return resource info
	 */
	public static ResourceInfo of(Object resource) {
		Class<?> c = resource.getClass();
		ResourceInfo info = of(c);
		String name = findValueByMethod(resource, ResourceName.class);
		if (name != null) {
			info.setName(name);
		}
		String id = findValueByMethod(resource, ResourceId.class);
		if (id != null) {
			info.setId(id);
		}
		return info;
	}
	public static ResourceInfo of(Class<?> cls) {
		ResourceInfo info = new ResourceInfo();
		ManagedResource r = cls.getAnnotation(ManagedResource.class);
		String domain = "";
		String type = "";
		String description = "";
		if (r != null) {
			domain = r.domain();
			type = r.type();
			description = r.description();
		}
		if (domain.isEmpty()) {
			domain = packageNameOf(cls);
		}
		if (type.isEmpty()) {
			type = classNameOf(cls);
		}
		info.setDomain(domain);
		info.setType(type);
		info.setDescription(description);
		info.setAttributes(findAttrs(cls));
		return info;
	}
	private static String packageNameOf(Class<?> cls) {
		String cname = cls.getName();
		return cname.substring(0, cname.lastIndexOf("."));
	}
	private static String classNameOf(Class<?> cls) {
		String cname = cls.getName();
		return cname.substring(cname.lastIndexOf(".") + 1);
	}
	private static String findValueByMethod(Object obj,
			Class<? extends Annotation> a) {
		Class<?> c = obj.getClass();
		Method m = findMethod(c, a);
		if (m != null) {
			try {
				Object value = m.invoke(obj);
				if (value != null) {
					return value.toString();
				}
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return null;
	}
	private static Method findMethod(Class<?> cls,
			Class<? extends Annotation> a) {
		for (Method m : cls.getMethods()) {
			if (m.getAnnotation(a) != null) {
				return m;
			}
		}
		return null;
	}
	private static ResourceAttribute[] findAttrs(Class<?> cls) {
		Map<String, ResourceAttribute> attrs =
				new HashMap<String, ResourceAttribute>();
		for (Method m : cls.getMethods()) {
			Getter g = m.getAnnotation(Getter.class);
			if (g != null) {
				String name = toAttrName(g, m);
				ResourceAttribute a = attrs.get(name);
				if (a == null) {
					a = new ResourceAttribute(name);
					attrs.put(name, a);
				}
				a.setGetter(m);
			} else {
				Setter s = m.getAnnotation(Setter.class);
				if (s != null) {
					String name = toAttrName(s, m);
					ResourceAttribute a = attrs.get(name);
					if (a == null) {
						a = new ResourceAttribute(name);
						attrs.put(name, a);
					}
					a.setSetter(m);
				}
			}
		}
		return attrs.values().toArray(
				new ResourceAttribute[attrs.size()]);
	}
	static String toAttrName(Getter g, Method m) {
		if (g.name().isEmpty()) {
			String methodName = m.getName();
			if (methodName.startsWith("get")) {
				return methodName.substring("get".length());
			} else {
				return methodName;
			}
		} else {
			return g.name();
		}
	}
	static String toAttrName(Setter s, Method m) {
		if (s.name().isEmpty()) {
			String methodName = m.getName();
			if (methodName.startsWith("set")) {
				return methodName.substring("set".length());
			} else {
				return methodName;
			}
		} else {
			return s.name();
		}
	}
}
