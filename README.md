# Sorm4j: Simple micro Object-Relation Mapper for Java

![Build](https://travis-ci.org/yuu-nkjm/sorm4j.svg?branch=master) [![Coverage Status](https://coveralls.io/repos/github/yuu-nkjm/sorm4j/badge.svg?branch=master&service=github)](https://coveralls.io/github/yuu-nkjm/sorm4j?branch=master) [![Maven Central](https://img.shields.io/maven-central/v/org.nkjmlab/sorm4j.svg)](http://mvnrepository.com/artifact/org.nkjmlab/sorm4j) [![javadoc](https://javadoc.io/badge2/org.nkjmlab/sorm4j/javadoc.svg)](https://javadoc.io/doc/org.nkjmlab/sorm4j) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


Sorm4j (Simple micro Object-Relation Mapper for Java) is a Java-based micro-ORM tool. It provides only simple functionalities to set POJO objects into `java.sql.PreparedStatement` parameters and map `java.sql.ResultSet` to POJO objects.

Sorm4j instance wraps a `java.sql.Connection` object. Sorm4j has only one dependency on a logging facade (SLF4J). Sorm4j is tested and evaluated performance with the H2 database. The results show a small overhead to comparing hand-coded JDBC operations. It means this tool is flexible and can be integrated with any code that depends on JDBC (including code that already uses another ORM tool).


## Central maven repository

You can get the binary using Maven from [Central Repository](http://mvnrepository.com/artifact/org.nkjmlab/sorm4j)

```xml
<dependency>
  <groupId>org.nkjmlab</groupId>
  <artifactId>sorm4j</artifactId>
  <version>1.0.0-rc5</version>
</dependency>
```

## Website
Check out the [Sorm4j website](https://scrapbox.io/sorm4j/) for quick start guide and examples.

- [Quickstart](https://scrapbox.io/sorm4j/Quickstart)
- [Examples](https://scrapbox.io/sorm4j/Examples): Sample codes are in [Sample of Sorm4j](https://github.com/yuu-nkjm/sorm4j-sample)
- [Performance](https://scrapbox.io/sorm4j/Performance): Benchmark codes are in [Benchmark of Sorm4j](https://github.com/yuu-nkjm/sorm4j-jmh)
- [Developer Guide](https://scrapbox.io/sorm4j/Developer_Guide)

## Versioning
This project uses [Semantic Versioning](https://semver.org/).

The interfaces and classes in the following packages are public API.

 - org.nkjmlab.sorm4j	
 - org.nkjmlab.sorm4j.annotation	
 - org.nkjmlab.sorm4j.extension	
 - org.nkjmlab.sorm4j.result	
 - org.nkjmlab.sorm4j.sqlstatement	

## License
Sorm4j is distributed under a [Apache License Version 2.0](https://github.com/yuu-nkjm/sorm4j/blob/master/LICENSE).

## Special thanks
* r5v9 for creating [r5v9/Persist (BSD license)](https://github.com/r5v9/persist), which inspires this project.
