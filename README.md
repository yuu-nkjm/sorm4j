# Sorm4j: Simple micro Object-Relation Mapper for Java [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) ![Build](https://travis-ci.org/yuu-nkjm/sorm4j.svg?branch=master)

Sorm4j (Simple micro Object-Relation Mapper for Java) is a Java-based micro-ORM tool. It provides only simple functionalities to map `java.sql.ResultSet` of the query to POJO objects and to set POJO objects into `java.sql.PreparedStatement` parameters.

Sorm4j instance wraps a `java.sql.Connection` object. Sorm4j has only one dependency on a logging facade (SLF4J). Sorm4j is tested and evaluated performance with the H2 database. The results show a small overhead to comparing hand-coded JDBC operations. It means this tool is very flexible and can be integrated with any code that depends on JDBC (including code that already uses another ORM tool).

## Quickstart
Check out the [Sorm4j website](https://scrapbox.io/sorm4j/) for quick start and examples.

## License
Sorm4j is distributed under a [Apache License Version 2.0](https://github.com/yuu-nkjm/sorm4j/LICENSE).

## Author
[yuu-nkjm](https://github.com/yuu-nkjm)

### Special thanks
* r5v9 for creating [r5v9/Persist (BSD license)](https://github.com/r5v9/persist), which inspires this project.
