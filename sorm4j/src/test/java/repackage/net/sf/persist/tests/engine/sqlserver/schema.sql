-- CREATE DATABASE sorm;

drop table if exists numeric_types, datetime_types, string_types, binary_types;

create table numeric_types (
	bit_col bit,
	tinyint_col tinyint,
	smallint_col smallint,
	int_col int,
	bigint_col bigint,
	smallmoney_col smallmoney,
	money_col money,
	decimal_col decimal,
	numeric_col numeric,
	float_col float,
	real_col real
);

create table datetime_types (
	datetime_col datetime
);

create table string_types (
	char_col char,
	varchar_col varchar(255),
	nchar_col nchar,
	nvarchar_col nvarchar(255)
);

create table binary_types (
	binary_col binary(255),
	varbinary_col varbinary(255),
	image_col image
);

