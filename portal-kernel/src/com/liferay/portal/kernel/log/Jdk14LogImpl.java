/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.log;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Brian Wing Shun Chan
 */
public class Jdk14LogImpl implements Log {

	public Jdk14LogImpl(Logger log) {
		_log = log;
	}

	@Override
	public void debug(Object msg) {
		_log(Level.FINE, msg, null);
	}

	@Override
	public void debug(Object msg, Throwable throwable) {
		_log(Level.FINE, msg, throwable);
	}

	@Override
	public void debug(Supplier<Object> msgSupplier) {
		_log(Level.FINE, msgSupplier, null);
	}

	@Override
	public void debug(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.FINE, msgSupplier, throwable);
	}

	@Override
	public void debug(Throwable throwable) {
		_log(Level.FINE, throwable, throwable);
	}

	@Override
	public void error(Object msg) {
		_log(Level.SEVERE, msg, null);
	}

	@Override
	public void error(Object msg, Throwable throwable) {
		_log(Level.SEVERE, msg, throwable);
	}

	@Override
	public void error(Supplier<Object> msgSupplier) {
		_log(Level.SEVERE, msgSupplier, null);
	}

	@Override
	public void error(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.SEVERE, msgSupplier, throwable);
	}

	@Override
	public void error(Throwable throwable) {
		_log(Level.SEVERE, throwable, throwable);
	}

	@Override
	public void fatal(Object msg) {
		_log(Level.SEVERE, msg, null);
	}

	@Override
	public void fatal(Object msg, Throwable throwable) {
		_log(Level.SEVERE, msg, throwable);
	}

	@Override
	public void fatal(Supplier<Object> msgSupplier) {
		_log(Level.SEVERE, msgSupplier, null);
	}

	@Override
	public void fatal(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.SEVERE, msgSupplier, throwable);
	}

	@Override
	public void fatal(Throwable throwable) {
		_log(Level.SEVERE, throwable, throwable);
	}

	public Logger getWrappedLogger() {
		return _log;
	}

	@Override
	public void info(Object msg) {
		_log(Level.INFO, msg, null);
	}

	@Override
	public void info(Object msg, Throwable throwable) {
		_log(Level.INFO, msg, throwable);
	}

	@Override
	public void info(Supplier<Object> msgSupplier) {
		_log(Level.INFO, msgSupplier, null);
	}

	@Override
	public void info(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.INFO, msgSupplier, throwable);
	}

	@Override
	public void info(Throwable throwable) {
		_log(Level.INFO, throwable, throwable);
	}

	@Override
	public boolean isDebugEnabled() {
		return _log.isLoggable(Level.FINE);
	}

	@Override
	public boolean isErrorEnabled() {
		return _log.isLoggable(Level.SEVERE);
	}

	@Override
	public boolean isFatalEnabled() {
		return _log.isLoggable(Level.SEVERE);
	}

	@Override
	public boolean isInfoEnabled() {
		return _log.isLoggable(Level.INFO);
	}

	@Override
	public boolean isTraceEnabled() {
		return _log.isLoggable(Level.FINEST);
	}

	@Override
	public boolean isWarnEnabled() {
		return _log.isLoggable(Level.WARNING);
	}

	@Override
	public void setLogWrapperClassName(String className) {
	}

	@Override
	public void trace(Object msg) {
		_log(Level.FINEST, msg, null);
	}

	@Override
	public void trace(Object msg, Throwable throwable) {
		_log(Level.FINEST, msg, throwable);
	}

	@Override
	public void trace(Supplier<Object> msgSupplier) {
		_log(Level.FINEST, msgSupplier, null);
	}

	@Override
	public void trace(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.FINEST, msgSupplier, throwable);
	}

	@Override
	public void trace(Throwable throwable) {
		_log(Level.FINEST, throwable, throwable);
	}

	@Override
	public void warn(Object msg) {
		_log(Level.WARNING, msg, null);
	}

	@Override
	public void warn(Object msg, Throwable throwable) {
		_log(Level.WARNING, msg, throwable);
	}

	@Override
	public void warn(Supplier<Object> msgSupplier) {
		_log(Level.WARNING, msgSupplier, null);
	}

	@Override
	public void warn(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.WARNING, msgSupplier, throwable);
	}

	@Override
	public void warn(Throwable throwable) {
		_log(Level.WARNING, throwable, throwable);
	}

	private void _log(Level level, Object msg, Throwable throwable) {
		if (!_log.isLoggable(level)) {
			return;
		}

		if (msg instanceof Supplier<?> supplier) {
			msg = supplier.get();
		}

		if (msg instanceof Throwable) {
			msg = throwable.getMessage();
		}

		_log.log(level, msg.toString(), throwable);
	}

	private final Logger _log;

}