
create table simple (
	id serial,
	string_col varchar(255),
	int_col int,
	primary key (id)
);

create table numeric_types (
	smallint_col smallint,
	integer_col integer,
	bigint_col bigint,
	decimal_col decimal,
	numeric_col numeric,
	real_col real,
	double_precision_col double precision,
	-- money_col money,
	-- bit_col bit,
	-- bit_varying_col bit varying,
	boolean_col boolean
);

create table string_types (
	char1_col char(1),
	char_col char(255),
	varchar_col varchar(255),
	text_col text,
	clob_col oid
);


create table datetime_types (
	date_col date,
	timestamp_col timestamp,
	time_col time
);

create table binary_types (
	bytea_col bytea,
	blob_col oid
);



