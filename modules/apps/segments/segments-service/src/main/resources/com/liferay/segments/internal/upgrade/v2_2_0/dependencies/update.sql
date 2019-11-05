create table SegmentsEntryRole (
	mvccVersion LONG default 0 not null,
	segmentsEntryRoleId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	segmentsEntryId LONG,
	roleId LONG
);

create index IX_65648B53 on SegmentsEntryRole (roleId);
create unique index IX_1E3D8394 on SegmentsEntryRole (segmentsEntryId, roleId);

COMMIT_TRANSACTION;