# Sorm4j: Simple micro Object-Relation Mapper for Java

![](https://i.gyazo.com/1f05d989533d039fb5b3920352a9da5d.png) ![Build](https://travis-ci.org/yuu-nkjm/sorm4j.svg?branch=master) [![Coverage Status](https://coveralls.io/repos/github/yuu-nkjm/sorm4j/badge.svg?branch=master&service=github)](https://coveralls.io/github/yuu-nkjm/sorm4j?branch=master) [![Maven Central](https://img.shields.io/maven-central/v/org.nkjmlab/sorm4j.svg)](http://mvnrepository.com/artifact/org.nkjmlab/sorm4j) [![javadoc](https://javadoc.io/badge2/org.nkjmlab/sorm4j/javadoc.svg)](https://javadoc.io/doc/org.nkjmlab/sorm4j) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Sorm4j (Simple micro Object-Relation Mapper for Java) is a Java-based micro-ORM tool. Sorm4j is a sort of JDBC wrapper. It provides simple functionalities to do select, insert, update, delete and merge.

Sorm4j sets Java objects into parameters of an SQL statement and executes the SQL statement, and it maps the result to Java objects. It opens a connection to a database and closes it after the execution automatically.

Here is an example with lambda expressions:

```java
// Creates an entry point as javax.sql.DataSource.
Sorm sorm = SormFactory.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;","username","password");

// insert
sorm.apply(conn -> conn.insert(new Customer(1, "Alice", "Tokyo")));

// select
List<Customer> customers =
  sorm.apply(conn -> conn.readList(Customer.class, "select * from customer where address=?","Tokyo"));

```

Sorm4j uses an object it simply wraps a `java.sql.Connection` object for object-relation mapping. Sorm4j has only one dependency on a logging facade (SLF4J). It means this tool can be integrated with any code that depends on JDBC (including code that already uses another ORM tool).

Here is an example with raw `java.sql.Connection`:

```java
try (Connection jdbcConn = DriverManager.getConnection(jdbcUrl, username, password)) {
  // Creates object-relation mapping tool to wrap JDBC connection.
  OrmConnection conn = SormFactory.toOrmConnection(jdbcConn);
  conn.insert(new Customer(1, "Alice", "Tokyo"));
  conn.readList(Customer.class, "select * from customer where address=?","Tokyo");
} catch (SQLException e) {
  e.printStackTrace();
}
```


Sorm4j is tested and evaluated performance with the H2 database. The results show a small overhead to comparing hand-coded JDBC operations.

Sorm4j requires Java 11 (or above) to run and build.


## Quickstart
The latest release is available at [Maven Central Repository](https://mvnrepository.com/artifact/org.nkjmlab/sorm4j). Add dependency to your pom.xml:

```xml
 <dependency>
   <groupId>org.nkjmlab</groupId>
   <artifactId>sorm4j</artifactId>
   <version>1.2.1</version>
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
Sorm sorm = SormFactory.create("jdbc:h2:mem:sormtest", "sa","");
```

Reads matching rows from table:

```java
List<Customer> list =
  sorm.apply(conn -> conn.readList(Customer.class, "select * from customer where id>?", 5));
```

Inserts a new row:

```java
sorm.apply(conn-> conn.insert(new Customer(1, "Alice", "Tokyo")));
```

## Performance (Oracle JMH)
![](https://i.gyazo.com/1030837229df0d24b301b84cd1df140f.png)![](https://i.gyazo.com/ec20038daf68db8e290c86c62be52234.png)
(The lower the better)

|lib|read (microsec/op)|insert (microsec/op)|
|:----|:----|:----|
|Hand coded (baseline)|5.8|6.2|
|Sorm4j|6.0 (3% slower)|7.1 (15% slower)|
|[Sql2o](https://github.com/aaberg/sql2o)|8.4 (45% slower)|11.1 (79% slower)|
|[JDBI](https://github.com/jdbi/jdbi)|18.4 (217% slower)|12.5 (102% slower)|
|[JOOQ](https://github.com/jOOQ/jOOQ)|35.8 (517% slower)|-|
|[MyBatis](https://github.com/mybatis/mybatis-3)|12.5 (116% slower)|-|


- read: reads one row from table including 10,240 row using primary key
- insert: inserts one row to table

If you need precise information, please take a look at the [Performance](https://scrapbox.io/sorm4j/Performance) page.

## Website
[Sorm4j website](https://scrapbox.io/sorm4j/) shows more information.

- [Quickstart](https://scrapbox.io/sorm4j/Quickstart)
    - To get started, see here.
- [Basic Usage](https://scrapbox.io/sorm4j/Basic_Usage)
    - Next to get started, see here.
- [Examples](https://scrapbox.io/sorm4j/Examples)
    - If you need more sample codes, please take a look at the [Examples](https://scrapbox.io/sorm4j/Examples) page and the [Sample of Sorm4j](https://github.com/yuu-nkjm/sorm4j-sample) repository.
- [Key Features](https://scrapbox.io/sorm4j/Key_Features)
    - The key features of Sorm4j, see here.
- [Performance](https://scrapbox.io/sorm4j/Performance)
    - If you need more benchmark results, please take a look at the [Performance](https://scrapbox.io/sorm4j/Performance) page and the [Benchmark of Sorm4j](https://github.com/yuu-nkjm/sorm4j-jmh) repository.
- [Developer's Guide](https://scrapbox.io/sorm4j/Developer's_Guide)
    - The detailed manual is here.

## Versioning
The interfaces and classes in the following packages are regarded as public API. If any methods are going to remove, they will be annotated by `@deprecated` and announced release note.

 - org.nkjmlab.sorm4j
 - org.nkjmlab.sorm4j.annotation
 - org.nkjmlab.sorm4j.extension
 - org.nkjmlab.sorm4j.sql

## License
Sorm4j is distributed under a [Apache License Version 2.0](https://github.com/yuu-nkjm/sorm4j/blob/master/LICENSE).

## Special thanks
- r5v9 for creating [r5v9/Persist (BSD license)](https://github.com/r5v9/persist), which inspires this project.
