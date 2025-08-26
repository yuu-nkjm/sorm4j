# Sorm4j: Simple micro Object-Relation Mapper for Java
![](https://i.gyazo.com/821728c9ea8eb75e3139448ddd81cc13.png)
[![Coverage Status](https://coveralls.io/repos/github/yuu-nkjm/sorm4j/badge.svg?branch=develop&service=github)](https://coveralls.io/github/yuu-nkjm/sorm4j?branch=develop)
 [![GitHub Package Registry](https://img.shields.io/badge/GitHub%20Packages-3.0.0-blue.svg)](https://github.com/yuu-nkjm/sorm4j/packages)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
[![Web Sites](https://img.shields.io/badge/Web%20Site-scrapbox-important.svg)](https://scrapbox.io/sorm4j/)

Sorm4j (Simple micro Object-Relation Mapper for Java) is a Java-based micro-ORM tool. Sorm4j is a sort of JDBC wrapper. It provides simple functionalities to select, insert, update, delete and merge.
Sorm4j sets Java objects into parameters of an SQL statement and executes the SQL statement, and maps the result to Java objects. It opens a connection to a database and closes it after the execution automatically.
Sorm4j uses an object it simply wraps a `java.sql.Connection` object for object-relation mapping. Sorm4j has no runtime dependencies. It means this tool can be integrated with any code that depends on JDBC (including code that already uses another ORM tool).
Sorm4j requires Java 17 (or above) after version 2.0.0.

## Quickstart
Add the following repository configuration to your `pom.xml` for using GitHub Packages:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/yuu-nkjm/</url>
  </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
  <groupId>org.nkjmlab</groupId>
  <artifactId>sorm4j</artifactId>
  <version>3.0.0</version>
</dependency>
```

We assume the following customer table in example: `create table customer (id int primary key, name varchar, address varchar)`

Create a Record class that has components and names matching the columns and the table name. For example:


```java
public record Customer (int id, String name, String address){}
```

You could also use a class with public fields and a default constructor matching a table name. For example:


```java
public class Customer {
  public int id;
  public String name;
  public String address;
}

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
| lib | read | insert | read multirow | insert multirow |
| - | - | - | - | - |
| Hand coded (baseline) | 4.5 | 5.1 | 3843 | 23448 |
| Sorm4j (2.0.7) | 3.8 (16% faster) | 5.5 (8% slower) | 3609 (6% faster) | 21521 (8% faster) |
| Sql2o | 7.0 (56% slower) | 9.5 (86% slower) | 4336 (13% slower) | 36309 (55% slower) |
| JDBI | 15.4 (242% slower) | 12.2 (139% slower) | 5564 (45% slower) | 32503 (39% slower) |
| JOOQ | 41.2 (816% slower) |  | 21559 (461% slower) |  |
| MyBatis | 10.6 (136% slower) |  | 9998 (160% slower) |  |
| Spring JdbcTemplate | 7.9 (76% slower) |  |  |  |

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
