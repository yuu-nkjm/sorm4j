package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sql.SqlKeyword.*;
import java.util.List;
import org.junit.jupiter.api.Test;


class SqlKeywordTest {

  @Test
  void test() {
    List<String> l = List.of(ALL, AND, ANY, AS, AUTHORIZATION, CASE, CHECK, COLLATE, COLUMN,
        CONSTRAINT, CREATE, CROSS, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP, CURRENT_USER,
        DEFAULT, DISTINCT, ELSE, END, EXCEPT, FETCH, FOR, FOREIGN, FROM, FULL, GRANT, GROUP, HAVING,
        IN, INNER, INTERSECT, INTO, IS, JOIN, LEFT, LIKE, NOT, NULL, ON, OR, ORDER, OUTER, PRIMARY,
        REFERENCES, RIGHT, SELECT, SESSION_USER, SOME, TABLE, TABLESAMPLE, THEN, TO, UNION, UNIQUE,
        USER, WHEN, WHERE, WITH);
    assertThat(l);
  }

}
