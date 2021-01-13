create table AccountGroupRel (
	mvccVersion LONG default 0 not null,
	AccountGroupRelId LONG not null primary key,
	companyId LONG,
	accountGroupId LONG,
	accountEntryId LONG
);

create index IX_8177283C on AccountGroupRel (accountEntryId);
create index IX_1FACB57D on AccountGroupRel (accountGroupId, accountEntryId);