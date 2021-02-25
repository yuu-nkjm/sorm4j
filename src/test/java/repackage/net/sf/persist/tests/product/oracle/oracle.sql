begin;

create table numeric_types (
	number_col number,
	binary_float_col float,
	binary_double_col double
);

create table datetime_types (
    --date_col date,
    timestamp_col timestamp
    --interval_year_col interval year to month,
    --interval_day_col interval day to second
);

create table string_types (
	char1_col char(1),
	char_col char(255),
	nchar1_col nchar,
	nchar_col nchar(255),
	nvarchar2_col nvarchar2(2000),
    varchar2_col varchar2(2000)
    --clob_col clob,
    --nclob_col nclob
);

create table binary_types (
	raw_col raw(2000),
	long_raw_col long raw,
	--rowid_col rowid,
	--urowid_col urowid,
	blob_col blob
	--bfile_col bfile
);

commit;
exit;

