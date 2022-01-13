# Sorm4j: Simple micro Object-Relation Mapper for Java

![](https://i.gyazo.com/1f05d989533d039fb5b3920352a9da5d.png)

![Build](https://travis-ci.org/yuu-nkjm/sorm4j.svg?branch=master) [![Coverage Status](https://coveralls.io/repos/github/yuu-nkjm/sorm4j/badge.svg?branch=master&service=github)](https://coveralls.io/github/yuu-nkjm/sorm4j?branch=master) [![Maven Central](https://img.shields.io/maven-central/v/org.nkjmlab/sorm4j.svg)](http://mvnrepository.com/artifact/org.nkjmlab/sorm4j) [![javadoc](https://javadoc.io/badge2/org.nkjmlab/sorm4j/javadoc.svg)](https://javadoc.io/doc/org.nkjmlab/sorm4j) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Web Sites](https://img.shields.io/badge/Web%20Site-scrapbox-important.svg)](https://scrapbox.io/sorm4j/)

Sorm4j (Simple micro Object-Relation Mapper for Java) is a Java-based micro-ORM tool. Sorm4j is a sort of JDBC wrapper. It provides simple functionalities to do select, insert, update, delete and merge.

Sorm4j sets Java objects into parameters of an SQL statement and executes the SQL statement, and it maps the result to Java objects. It opens a connection to a database and closes it after the execution automatically.

Here is a simple example:

```java
// Creates an entry point.
Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;","username","password");

// insert
sorm.insert(new Customer(1, "Alice", "Tokyo"));

// select
List<Customer> customers =
  sorm.readList(Customer.class, "select * from customer where address=?","Tokyo");
```

Sorm4j uses an object it simply wraps a `java.sql.Connection` object for object-relation mapping. Sorm4j has no runtime dependencies. It means this tool can be integrated with any code that depends on JDBC (including code that already uses another ORM tool).

Sorm4j requires Java 11 (or above) to run and build.


## Quickstart
The latest release is available at [Maven Central Repository](https://mvnrepository.com/artifact/org.nkjmlab/sorm4j). Add dependency to your `pom.xml`:

```xml
 <dependency>
   <groupId>org.nkjmlab</groupId>
   <artifactId>sorm4j</artifactId>
   <version>1.4.0</version>
 </dependency>
```

Create a class with public fields and default constructor matching a table name. For example:

```java
public class Customer {
  public int id;
  public String name;
  public String address;
}
```

Create an entry point:

```java
Sorm sorm = Sorm.create("jdbc:h2:mem:sormtest", "sa","");
```

Reads matching rows from table and maps to Java object:

```java
List<Customer> list =
  sorm.readList(Customer.class, "select * from customer where id>?", 5);
```

Inserts a new row with Java object:

```java
sorm.insert(new Customer(1, "Alice", "Tokyo"));
```

## Benchmarking with Oracle JMH (average operation times: microsec/op) (1.4.0)

| lib|read|insert|read multirow|insert multirow|
|:----|:----|:----|:----|:----|
|Hand coded (baseline)|4.2 |5.1 |3349 |20948 |
|Sorm4j|4.3 (2% slower)|5.5 (8% slower)|3101 (-7% slower)|20982 (0% slower)|
|Sql2o|6.3 (50% slower)|9.2 (80% slower)|3892 (16% slower)|40654 (94% slower)|
|JDBI|16.3 (288% slower)|11.8 (131% slower)|5036 (50% slower)|36909 (76% slower)|
|JOOQ|42.0 (900% slower)| |14005 (318% slower)||
|MyBatis|9.4 (124% slower)| |10199 (205% slower)||
|Spring JdbcTemplate|8.6 (105% slower)| |||

- `read`: reads one row from table including 10,240 rows.
- `insert`: inserts one row to table.
- `read multirow`: reads all rows from table including 10,240 rows.
- `insert multirow`: inserts the all given 10,240 rows to table.

Sorm4j is evaluated performance with the H2 database. The results show a small overhead to comparing hand-coded JDBC operations. If you need precise information, please take a look at the [Performance](https://scrapbox.io/sorm4j/Performance) page.

## Website
[Sorm4j website](https://scrapbox.io/sorm4j/) shows more information.

- [Quickstart](https://scrapbox.io/sorm4j/Quickstart)
    - To get started, see here.
- [Examples](https://scrapbox.io/sorm4j/Examples)
    - If you need more sample codes, please take a look at the [Examples](https://scrapbox.io/sorm4j/Examples) page and the [example codes of Sorm4j](https://github.com/yuu-nkjm/sorm4j/tree/master/sorm4j-example).
- [Performance](https://scrapbox.io/sorm4j/Performance)
    - If you need more benchmark results, please take a look at the [Performance](https://scrapbox.io/sorm4j/Performance) page and the [JMH benchmark codes of Sorm4j](https://github.com/yuu-nkjm/sorm4j/tree/master/sorm4j-jmh).

## License
Sorm4j is distributed under a [Apache License Version 2.0](https://github.com/yuu-nkjm/sorm4j/blob/master/LICENSE).

## Special thanks
- r5v9 for creating [r5v9/Persist (BSD license)](https://github.com/r5v9/persist), which inspires this project.
