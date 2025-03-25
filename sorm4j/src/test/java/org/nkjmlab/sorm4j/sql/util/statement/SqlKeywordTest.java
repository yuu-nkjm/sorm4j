package org.nkjmlab.sorm4j.sql.util.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.ALL;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.AND;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.ANY;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.AS;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.AUTHORIZATION;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CASE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CHECK;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.COLLATE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.COLUMN;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CONSTRAINT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CREATE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CROSS;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CURRENT_DATE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CURRENT_TIME;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CURRENT_TIMESTAMP;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.CURRENT_USER;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.DEFAULT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.DISTINCT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.ELSE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.END;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.EXCEPT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.FETCH;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.FOR;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.FOREIGN;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.FROM;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.FULL;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.GRANT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.GROUP;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.HAVING;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.IN;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.INNER;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.INTERSECT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.INTO;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.IS;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.JOIN;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.LEFT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.LIKE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.NOT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.NULL;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.ON;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.OR;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.ORDER;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.OUTER;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.PRIMARY;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.REFERENCES;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.RIGHT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.SELECT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.SESSION_USER;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.SOME;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.TABLE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.TABLESAMPLE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.THEN;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.TO;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.UNION;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.UNIQUE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.USER;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.WHEN;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.WHERE;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.WITH;

import java.util.List;

import org.junit.jupiter.api.Test;

class SqlKeywordTest {

  @Test
  void test() {
    List<String> l =
        List.of(
            ALL,
            AND,
            ANY,
            AS,
            AUTHORIZATION,
            CASE,
            CHECK,
            COLLATE,
            COLUMN,
            CONSTRAINT,
            CREATE,
            CROSS,
            CURRENT_DATE,
            CURRENT_TIME,
            CURRENT_TIMESTAMP,
            CURRENT_USER,
            DEFAULT,
            DISTINCT,
            ELSE,
            END,
            EXCEPT,
            FETCH,
            FOR,
            FOREIGN,
            FROM,
            FULL,
            GRANT,
            GROUP,
            HAVING,
            IN,
            INNER,
            INTERSECT,
            INTO,
            IS,
            JOIN,
            LEFT,
            LIKE,
            NOT,
            NULL,
            ON,
            OR,
            ORDER,
            OUTER,
            PRIMARY,
            REFERENCES,
            RIGHT,
            SELECT,
            SESSION_USER,
            SOME,
            TABLE,
            TABLESAMPLE,
            THEN,
            TO,
            UNION,
            UNIQUE,
            USER,
            WHEN,
            WHERE,
            WITH);
    assertThat(l);
  }
}
