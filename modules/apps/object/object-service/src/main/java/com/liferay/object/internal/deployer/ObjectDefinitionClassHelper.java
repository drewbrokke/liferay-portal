/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.object.internal.deployer;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryWrapper;
import com.liferay.petra.reflect.ReflectionUtil;
 import net.bytebuddy.ByteBuddy;
 import net.bytebuddy.dynamic.DynamicType;
 import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
 import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Drew Brokke
 */
public class ObjectDefinitionClassHelper {

	public ObjectDefinitionClassHelper(ObjectDefinition objectDefinition) {

		String objectDefinitionClassName = objectDefinition.getClassName();

		String newClassName =
			objectDefinitionClassName.replace("#", "_") +
				objectDefinition.getName();

		_objectClass = _getClass(newClassName);

		try {
			_constructor = _objectClass.getConstructor(ObjectEntry.class);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private Class<? extends ObjectEntry> _getClass(String className) {
		ClassLoader classLoader = ObjectEntryWrapper.class.getClassLoader();

		try {
			return (Class<? extends ObjectEntry>)classLoader.loadClass(className);
		}
		catch (ClassNotFoundException e) {
			System.out.println("No class found, creating a new one");
		}

		return new ByteBuddy(
		).subclass(
			ObjectEntryWrapper.class,
			ConstructorStrategy.Default.IMITATE_SUPER_CLASS
		).name(
			className
		).make(
		).load(
			classLoader,
			ClassLoadingStrategy.Default.INJECTION
		).getLoaded();
	}


	public Class<? extends ObjectEntry> getObjectClass() {
		return _objectClass;
	}

	public ObjectEntry wrap(ObjectEntry objectEntry) {
		try {
			return _constructor.newInstance(objectEntry);
		}
		catch (InstantiationException | InvocationTargetException |
			   IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	private final Class<? extends ObjectEntry> _objectClass;
	private final Constructor<? extends ObjectEntry> _constructor;
}
