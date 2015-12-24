package com.nec.strudel.tkvs;

import java.util.List;

import com.nec.strudel.entity.info.BeanInfo;
import com.nec.strudel.entity.info.ValueInfo;
import com.nec.strudel.entity.info.ValueTypes;

public abstract class KeyConstructor {

	public abstract <T> T createKey(Key key);
	public abstract Key toKey(Object key);

	public static KeyConstructor constructorOf(Class<?> keyClass) {
		if (ValueTypes.isPrimitive(keyClass)) {
			return new PrimitiveKeyConstructor(keyClass);
		} else {
			return new BeanKeyConstructor(new BeanInfo(keyClass));
		}
	}
	public static KeyConstructor constructorOf(ValueInfo info) {
		if (info.isPrimitive()) {
			return new PrimitiveKeyConstructor(info.valueClass());
		} else {
			return new BeanKeyConstructor((BeanInfo) info);
		}
	}
	static class BeanKeyConstructor extends KeyConstructor {
		private final BeanInfo info;
		private final Class<?>[] types;

		public BeanKeyConstructor(BeanInfo info) {
			this.info = info;
			List<Class<?>> list = info.types();
			types = list.toArray(new Class<?>[list.size()]);
		}
		@SuppressWarnings("unchecked")
		@Override
		public <T> T createKey(Key key) {
			return (T) info.create(key.toTuple(types));
		}
		@Override
		public Key toKey(Object key) {
			return Key.create(info.toTuple(key));
		}
	}
	static class PrimitiveKeyConstructor extends KeyConstructor {
		private final Class<?> keyClass;
		public PrimitiveKeyConstructor(Class<?> keyClass) {
			this.keyClass = keyClass;
		}
		@SuppressWarnings("unchecked")
		@Override
		public <T> T createKey(Key key) {
			return key.convert((Class<T>) keyClass);
		}
		public Key toKey(Object key) {
			return Key.create(key);
		}
		
	}
}
