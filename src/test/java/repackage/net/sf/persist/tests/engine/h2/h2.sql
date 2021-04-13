drop table simple if exists;

create table if not exists simple (
	id int auto_increment,
	string_col varchar(255),
	long_col long,
	primary key (id)
);

drop table numeric_types if exists;

create table if not exists numeric_types (
	int_col int,
	boolean_col boolean,
	tinyint_col tinyint,
	smallint_col smallint,
	bigint_col bigint,
	decimal_col decimal,
	double_col double,
	real_col real
);

drop table datetime_types if exists;

create table if not exists datetime_types (
	time_col time,
	date_col date,
	timestamp_col timestamp
);

drop table binary_types if exists;

create table if not exists binary_types (
	binary_col binary,
	blob_col blob,
	other_col other
	--uuid_col uuid
); 

drop table string_types if exists;

create table if not exists string_types (
	varchar_col varchar,
	varchar_ignorecase_col varchar_ignorecase,
	char_col char,
	clob_col clob
);


drop table all_types if exists;

create table if not exists all_types (

	int_col int,
	boolean_col boolean,
	tinyint_col tinyint,
	smallint_col smallint,
	bigint_col bigint,
	decimal_col decimal,
	double_col double,
	real_col real,

	time_col time,
	date_col date,
	timestamp_col timestamp,

	binary_col binary,
	blob_col blob,
	other_col other,
	uuid_col uuid,

	varchar_col varchar,
	varchar_ignorecase_col varchar_ignorecase,
	char_col char,
	clob_col clob,

	id int auto_increment
);
