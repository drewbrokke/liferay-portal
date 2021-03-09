package com.liferay.account.internal.search.spi.model.permission;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.spi.model.permission.SearchPermissionFilterContributor;
import com.liferay.portal.vulcan.util.TransformUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

@Component(
	immediate = true,
	service = SearchPermissionFilterContributor.class
)
public class AccountEntrySearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountUserId(userId);

		if (ListUtil.isNotEmpty(accountEntryUserRels)) {
			TermsFilter primaryKeysTermsFilter = new TermsFilter(
				Field.ENTRY_CLASS_PK);

			primaryKeysTermsFilter.addValues(
				TransformUtil.transform(
					accountEntryUserRels,
					accountEntryUserRel ->
						String.valueOf(
							accountEntryUserRel.getAccountEntryId())).toArray(
								new String[accountEntryUserRels.size()]));

			booleanFilter.add(primaryKeysTermsFilter, BooleanClauseOccur.MUST);

			TermsFilter termsFilter = new TermsFilter("accountRoleIds");

			Role accountMemberRole = _roleLocalService.fetchRole(
				companyId,
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER);

			termsFilter.addValue(String.valueOf(accountMemberRole.getRoleId()));

			booleanFilter.add(termsFilter);
		}
	}

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}
