/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.object.internal.deployer;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryWrapper;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;

/**
 * @author Drew Brokke
 */
public class ObjectDefinitionClassHelper {

	public ObjectDefinitionClassHelper(ObjectDefinition objectDefinition) {
		String objectDefinitionClassName = StringUtil.replace(
			objectDefinition.getClassName(), CharPool.POUND,
			CharPool.UNDERLINE);

		_objectClass = _getClass(
			objectDefinitionClassName + objectDefinition.getName());

		try {
			_constructor = _objectClass.getConstructor(ObjectEntry.class);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public Class<? extends ObjectEntry> getObjectClass() {
		return _objectClass;
	}

	public ObjectEntry wrap(ObjectEntry objectEntry) {
		try {
			return _constructor.newInstance(objectEntry);
		}
		catch (IllegalAccessException | InstantiationException |
			   InvocationTargetException e) {

			throw new RuntimeException(e);
		}
	}

	private Class<? extends ObjectEntry> _getClass(String className) {
		ClassLoader classLoader =
			ObjectDefinitionClassHelper.class.getClassLoader();

		try {
			return (Class<? extends ObjectEntry>)classLoader.loadClass(
				className);
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
			classLoader, ClassLoadingStrategy.Default.INJECTION
		).getLoaded();
	}

	private final Constructor<? extends ObjectEntry> _constructor;
	private final Class<? extends ObjectEntry> _objectClass;

}