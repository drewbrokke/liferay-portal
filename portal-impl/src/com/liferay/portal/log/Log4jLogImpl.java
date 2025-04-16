/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.log;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogWrapper;

import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * @author Brian Wing Shun Chan
 */
public class Log4jLogImpl implements Log {

	public Log4jLogImpl(Logger logger) {
		_logger = (org.apache.logging.log4j.core.Logger)logger;
	}

	@Override
	public void debug(Object msg) {
		_log(Level.DEBUG, msg, null);
	}

	@Override
	public void debug(Object msg, Throwable throwable) {
		_log(Level.DEBUG, msg, throwable);
	}

	@Override
	public void debug(Supplier<Object> msgSupplier) {
		_log(Level.DEBUG, msgSupplier, null);
	}

	@Override
	public void debug(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.DEBUG, msgSupplier, throwable);
	}

	@Override
	public void debug(Throwable throwable) {
		_log(Level.DEBUG, throwable, throwable);
	}

	@Override
	public void error(Object msg) {
		_log(Level.ERROR, msg, null);
	}

	@Override
	public void error(Object msg, Throwable throwable) {
		_log(Level.ERROR, msg, throwable);
	}

	@Override
	public void error(Supplier<Object> msgSupplier) {
		_log(Level.ERROR, msgSupplier, null);
	}

	@Override
	public void error(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.ERROR, msgSupplier, throwable);
	}

	@Override
	public void error(Throwable throwable) {
		_log(Level.ERROR, throwable, throwable);
	}

	@Override
	public void fatal(Object msg) {
		_log(Level.FATAL, msg, null);
	}

	@Override
	public void fatal(Object msg, Throwable throwable) {
		_log(Level.FATAL, msg, throwable);
	}

	@Override
	public void fatal(Supplier<Object> msgSupplier) {
		_log(Level.FATAL, msgSupplier, null);
	}

	@Override
	public void fatal(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.FATAL, msgSupplier, throwable);
	}

	@Override
	public void fatal(Throwable throwable) {
		_log(Level.FATAL, throwable, throwable);
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
		return _logger.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return _logger.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return _logger.isFatalEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return _logger.isInfoEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return _logger.isTraceEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return _logger.isWarnEnabled();
	}

	@Override
	public void setLogWrapperClassName(String className) {
		_logWrapperClassName = className;
	}

	@Override
	public void trace(Object msg) {
		_log(Level.TRACE, msg, null);
	}

	@Override
	public void trace(Object msg, Throwable throwable) {
		_log(Level.TRACE, msg, throwable);
	}

	@Override
	public void trace(Supplier<Object> msgSupplier) {
		_log(Level.TRACE, msgSupplier, null);
	}

	@Override
	public void trace(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.TRACE, msgSupplier, throwable);
	}

	@Override
	public void trace(Throwable throwable) {
		_log(Level.TRACE, throwable, throwable);
	}

	@Override
	public void warn(Object msg) {
		_log(Level.WARN, msg, null);
	}

	@Override
	public void warn(Object msg, Throwable throwable) {
		_log(Level.WARN, msg, throwable);
	}

	@Override
	public void warn(Supplier<Object> msgSupplier) {
		_log(Level.WARN, msgSupplier, null);
	}

	@Override
	public void warn(Supplier<Object> msgSupplier, Throwable throwable) {
		_log(Level.WARN, msgSupplier, throwable);
	}

	@Override
	public void warn(Throwable throwable) {
		_log(Level.WARN, throwable, throwable);
	}

	private void _log(Level level, Object msg, Throwable throwable) {
		if (msg instanceof Supplier<?> supplier) {
			_logger.logIfEnabled(
				_logWrapperClassName, level, null, supplier, throwable);

			return;
		}

		if (msg instanceof Throwable) {
			_logger.logIfEnabled(
				_logWrapperClassName, level, null, throwable::getMessage,
				throwable);

			return;
		}

		_logger.logIfEnabled(_logWrapperClassName, level, null, msg, throwable);
	}

	private final org.apache.logging.log4j.core.Logger _logger;
	private String _logWrapperClassName = LogWrapper.class.getName();

}