create table Address (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	addressId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	name VARCHAR(75) null,
	description VARCHAR(75) null,
	street1 VARCHAR(75) null,
	street2 VARCHAR(75) null,
	street3 VARCHAR(75) null,
	city VARCHAR(75) null,
	zip VARCHAR(75) null,
	regionId LONG,
	countryId LONG,
	latitude DOUBLE,
	longitude DOUBLE,
	typeId LONG,
	mailing BOOLEAN,
	primary_ BOOLEAN
);

create index IX_CBAD282F on Address (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_15411410 on Address (uuid_[$COLUMN_LENGTH:75$], groupId);

COMMIT_TRANSACTION;