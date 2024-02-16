/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membershippolicy;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.membershippolicy.RoleMembershipPolicy;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.util.PropsValues;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Roberto Díaz
 */
public class RoleMembershipPolicyFactoryUtil {

	public static RoleMembershipPolicy getRoleMembershipPolicy() {
		ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
			serviceTracker = _serviceTrackerDCLSingleton.getSingleton(
				RoleMembershipPolicyFactoryUtil::_createServiceTracker);

		CompanyIntegrityChecker companyIntegrityChecker =
			_companyIntegrityCheckerDCLSingleton.getSingleton(
				RoleMembershipPolicyFactoryUtil::
					_createCompanyIntegrityChecker);

		return new CompanyCheckRoleMembershipPolicy(
			companyIntegrityChecker, serviceTracker.getService());
	}

	private static CompanyIntegrityChecker _createCompanyIntegrityChecker() {
		return new CompanyIntegrityChecker(
			_getPersistedModelLocalService(User.class));
	}

	private static ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
		_createServiceTracker() {

		ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>
			serviceTracker = new ServiceTracker<>(
				_bundleContext, RoleMembershipPolicy.class,
				new RoleMembershipPolicyTrackerCustomizer());

		serviceTracker.open();

		return serviceTracker;
	}

	private static PersistedModelLocalService _getPersistedModelLocalService(
		Class<? extends ShardedModel> clazz) {

		return Objects.requireNonNull(
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(clazz.getName()),
			"No local service found for class " + clazz.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RoleMembershipPolicyFactoryUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final DCLSingleton<CompanyIntegrityChecker>
		_companyIntegrityCheckerDCLSingleton = new DCLSingleton<>();
	private static final DCLSingleton
		<ServiceTracker<RoleMembershipPolicy, RoleMembershipPolicy>>
			_serviceTrackerDCLSingleton = new DCLSingleton<>();

	private static class CompanyCheckRoleMembershipPolicy
		implements RoleMembershipPolicy {

		private long _getCompanyId(long roleId) throws PortalException {
			Role role = RoleLocalServiceUtil.getRole(roleId);

			return role.getCompanyId();
		}

		@Override
		public void checkRoles(
				long[] userIds, long[] addRoleIds, long[] removeRoleIds)
			throws PortalException {

			_check(userIds, addRoleIds);

			_roleMembershipPolicy.checkRoles(
				userIds, addRoleIds, removeRoleIds);
		}

		@Override
		public boolean isRoleAllowed(long userId, long roleId)
			throws PortalException {

			if (!_companyIntegrityChecker.isValid(_getCompanyId(roleId), userId)) {
				return false;
			}

			return _roleMembershipPolicy.isRoleAllowed(userId, roleId);
		}

		@Override
		public boolean isRoleRequired(long userId, long roleId)
			throws PortalException {

			return _roleMembershipPolicy.isRoleRequired(userId, roleId);
		}

		@Override
		public void propagateRoles(
				long[] userIds, long[] addRoleIds, long[] removeRoleIds)
			throws PortalException {

			_check(userIds, addRoleIds);

			_roleMembershipPolicy.propagateRoles(
				userIds, addRoleIds, removeRoleIds);
		}

		private void _check(long[] userIds, long[] addRoleIds)
			throws PortalException {
			for (long addRoleId : addRoleIds) {
				_companyIntegrityChecker.check(
					_getCompanyId(addRoleId), userIds);
			}
		}

		@Override
		public void verifyPolicy() throws PortalException {
			_roleMembershipPolicy.verifyPolicy();
		}

		@Override
		public void verifyPolicy(Role role) throws PortalException {
			_roleMembershipPolicy.verifyPolicy(role);
		}

		@Override
		public void verifyPolicy(
				Role role, Role oldRole,
				Map<String, Serializable> oldExpandoAttributes)
			throws PortalException {

			_roleMembershipPolicy.verifyPolicy(
				role, oldRole, oldExpandoAttributes);
		}

		private CompanyCheckRoleMembershipPolicy(
			CompanyIntegrityChecker companyIntegrityChecker,
			RoleMembershipPolicy roleMembershipPolicy) {

			_companyIntegrityChecker = companyIntegrityChecker;
			_roleMembershipPolicy = roleMembershipPolicy;
		}

		private final CompanyIntegrityChecker _companyIntegrityChecker;
		private final RoleMembershipPolicy _roleMembershipPolicy;

	}

	private static class CompanyIntegrityChecker {

		public void check(long companyId, long classPk) throws PortalException {
			if (!isValid(companyId, classPk)) {
				throw new PortalException();
			}
		}

		public void check(long companyId, long[] classPks)
			throws PortalException {

				for (long classPk2 : classPks) {
					check(companyId, classPk2);
				}
		}

		public boolean isValid(long companyId, long classPk)
			throws PortalException {

			ShardedModel shardedModel =
				(ShardedModel)_persistedModelLocalService.getPersistedModel(
					classPk);


			if (companyId == shardedModel.getCompanyId()) {
				return true;
			}

			return false;
		}

		private CompanyIntegrityChecker(
			PersistedModelLocalService persistedModelLocalService) {

			_persistedModelLocalService = persistedModelLocalService;
		}

		private final PersistedModelLocalService _persistedModelLocalService;

	}

	private static class RoleMembershipPolicyTrackerCustomizer
		implements ServiceTrackerCustomizer
			<RoleMembershipPolicy, RoleMembershipPolicy> {

		@Override
		public RoleMembershipPolicy addingService(
			ServiceReference<RoleMembershipPolicy> serviceReference) {

			RoleMembershipPolicy roleMembershipPolicy =
				_bundleContext.getService(serviceReference);

			if (PropsValues.MEMBERSHIP_POLICY_AUTO_VERIFY) {
				try {
					roleMembershipPolicy.verifyPolicy();
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}

			return roleMembershipPolicy;
		}

		@Override
		public void modifiedService(
			ServiceReference<RoleMembershipPolicy> serviceReference,
			RoleMembershipPolicy roleMembershipPolicy) {
		}

		@Override
		public void removedService(
			ServiceReference<RoleMembershipPolicy> serviceReference,
			RoleMembershipPolicy roleMembershipPolicy) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}