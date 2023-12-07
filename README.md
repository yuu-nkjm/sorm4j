# Sorm4j: Simple micro Object-Relation Mapper for Java

![](https://i.gyazo.com/1f05d989533d039fb5b3920352a9da5d.png)

![Build](https://travis-ci.org/yuu-nkjm/sorm4j.svg?branch=develop) [![Coverage Status](https://coveralls.io/repos/github/yuu-nkjm/sorm4j/badge.svg?branch=develop&service=github)](https://coveralls.io/github/yuu-nkjm/sorm4j?branch=develop) [![Maven Central](https://img.shields.io/maven-central/v/org.nkjmlab/sorm4j.svg)](http://mvnrepository.com/artifact/org.nkjmlab/sorm4j) [![javadoc](https://javadoc.io/badge2/org.nkjmlab/sorm4j/javadoc.svg)](https://javadoc.io/doc/org.nkjmlab/sorm4j) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Web Sites](https://img.shields.io/badge/Web%20Site-scrapbox-important.svg)](https://scrapbox.io/sorm4j/)

Sorm4j (Simple micro Object-Relation Mapper for Java) is a Java-based micro-ORM tool. Sorm4j is a sort of JDBC wrapper. It provides simple functionalities to select, insert, update, delete and merge.

Sorm4j sets Java objects into parameters of an SQL statement and executes the SQL statement, and maps the result to Java objects. It opens a connection to a database and closes it after the execution automatically.

Sorm4j uses an object it simply wraps a `java.sql.Connection` object for object-relation mapping. Sorm4j has no runtime dependencies. It means this tool can be integrated with any code that depends on JDBC (including code that already uses another ORM tool).

Sorm4j requires Java 17 (or above) after version 2.0.0.


## Quickstart
The latest release is available at [Maven Central Repository](https://mvnrepository.com/artifact/org.nkjmlab/sorm4j). Add dependency to your `pom.xml`:

```xml
 <dependency>
   <groupId>org.nkjmlab</groupId>
   <artifactId>sorm4j</artifactId>
   <version>2.0.6</version>
 </dependency>
```
We assume the following customer table in example: `create table customer (id int primary key, name varchar, address varchar)`

Create a class with public fields and a default constructor matching a table name. For example:

```java
public class Customer {
  public int id;
  public String name;
  public String address;
}
```

You could also use Record class. For example:

```java
 @OrmRecord
 public record Customer (int id, String name, String address){}
```

Create an entry point:

```java
Sorm sorm = Sorm.create("jdbc:h2:mem:sormtest;DB_CLOSE_DELAY=-1");
```

Reads matching rows from table and maps to Java object:

```java
List<Customer> list =
  sorm.readList(Customer.class, "select * from customer where id>?", 5);
```
Handles ResultSet as Stream object:

```java
Map<String, Customer> tmp = sorm.stream(Customer.class, "select * from customer where id>?", 1)
    .apply(stream -> stream.collect(Collectors.toMap(c -> c.getName(), c -> c)));
```

Inserts new rows with Java object (the table name is guessed from the class name):

```java
sorm.insert(new Customer(1, "Alice", "Tokyo"), new Customer(2, "Bob", "Tokyo"));
```

## Benchmarking with Oracle JMH (average operation times: microsecond/op)

| lib|read|insert|read multirow|insert multirow|
|:----|:----|:----|:----|:----|
|Hand coded (baseline)|4.4 |5.4 |3427 |23642 |
|Sorm4j (1.4.9)|3.8 (-14% slower)|6.0 (11% slower)|3219 (-6% slower)|21898 (-7% slower)|


| lib|read|insert|read multirow|insert multirow|
|:----|:----|:----|:----|:----|
|Hand coded (baseline)|4.4 |5.6 |3528 |24312 |
|Sorm4j (1.4.6)|3.8 (-14% slower)|6.1 (9% slower)|3175 (-10% slower)|21580 (-11% slower)|
|Sql2o (1.6.0)|6.5 (48% slower)|9.7 (73% slower)|3871 (10% slower)|42668 (76% slower)|
|JDBI (3.25.0)|17.0 (286% slower)|12.7 (127% slower)|5487 (56% slower)|37723 (55% slower)|
|JOOQ (3.15.5)|44.2 (905% slower)| |14440 (309% slower)||
|MyBatis (3.5.8)|10.2 (132% slower)| |10440 (196% slower)||
|Spring JdbcTemplate (5.3.14)|9.1 (107% slower)| |||

- `read`: reads one row from the table including 10,240 rows.
- `insert`: inserts one row to the table.
- `read multirow`: reads all rows from the table including 10,240 rows.
- `insert multirow`: inserts all given 10,240 rows to the table.

Sorm4j is evaluated performance with the H2 database. The results show a small overhead to comparing hand-coded JDBC operations. If you need precise information, please look at the [Performance](https://scrapbox.io/sorm4j/Performance) page.

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
