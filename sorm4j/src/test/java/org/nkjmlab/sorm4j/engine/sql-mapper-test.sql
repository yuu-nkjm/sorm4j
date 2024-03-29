 drop table if exists sql_mapper_test;

 create table sql_mapper_test (
     -- simple
     c_boolean boolean,
     c_integer integer,
     c_decimal decimal (3,3),
     c_double double precision,
     c_varchar varchar(100),
     c_text text,
     c_bytea bytea,
     c_uuid uuid,
     -- datetime
     c_date date,
     c_time time,
     c_timetz time with time zone,
     c_timestamp timestamp,
     c_timestamptz timestamp with time zone,
     -- complex
     -- c_inet_ipv4 inet,
     -- c_inet_ipv6 inet,
     -- c_url url,
     c_integer_array integer ARRAY,
     c_varchar_array varchar ARRAY
 );

 insert into sql_mapper_test (
     -- simple
     c_boolean,
     c_integer,
     c_decimal,
     c_double,
     c_varchar,
     c_text,
     c_bytea,
     c_uuid,
     -- datetime
     c_date,
     c_time,
     c_timetz,
     c_timestamp,
     c_timestamptz,
     -- complex
     -- c_inet_ipv4,
     -- c_inet_ipv6,
     -- c_url,
     c_integer_array,
     c_varchar_array
 ) values (
     -- simple
     true,
     1,
     '0.5',
     0.5,
     'varchar',
     'long long text',
     x'DEADBEEF',
     '33ee757a-19b3-45dc-be79-f1d65ac5d1a4',
     -- datetime
     '2019-09-27',
     '13:23:00',
     '13:23:00+09:00',
     '2019-09-27T13:23:00',
     '2019-09-27T13:23:00+09:00',
     -- complex
     -- inet '192.168.1.1',
     -- inet '::1',
     -- 'https://example.com',
     array[1, 2, 3],
     array['A', 'B', 'C']
 );