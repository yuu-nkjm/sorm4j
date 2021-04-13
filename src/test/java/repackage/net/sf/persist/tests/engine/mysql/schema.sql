drop table if exists numeric_types, datetime_types, string_types, binary_types;

create table numeric_types (
  bit_col bit,
  tinyint_col tinyint,
  boolean_col boolean,
  smallint_col smallint,
  mediumint_col mediumint,
  int_col int,
  bigint_col bigint,
  float_col float,
  double_col double,
  decimal_col decimal
);

create table datetime_types (
  date_col date,
  datetime_col datetime,
  time_col time,
  year4_col year
);

create table string_types (
  char_col char(255),
  varchar_col varchar(255),
  tinytext_col tinytext,
  text_col text,
  mediumtext_col mediumtext,
  longtext_col longtext,
  enum_col enum('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')
);

create table binary_types (
  binary_col binary(255),
  varbinary_col varbinary(255),
  tinyblob_col tinyblob,
  blob_col blob,
  mediumblob_col mediumblob,
  longblob_col longblob
);

