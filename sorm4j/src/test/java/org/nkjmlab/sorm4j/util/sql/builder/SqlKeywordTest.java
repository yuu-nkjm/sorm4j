package org.nkjmlab.sorm4j.util.sql.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.ALL;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.AND;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.ANY;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.AS;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.AUTHORIZATION;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CASE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CHECK;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.COLLATE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.COLUMN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CONSTRAINT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CREATE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CROSS;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CURRENT_DATE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CURRENT_TIME;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CURRENT_TIMESTAMP;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.CURRENT_USER;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.DEFAULT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.DISTINCT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.ELSE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.END;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.EXCEPT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.FETCH;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.FOR;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.FOREIGN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.FROM;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.FULL;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.GRANT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.GROUP;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.HAVING;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.IN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.INNER;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.INTERSECT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.INTO;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.IS;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.JOIN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.LEFT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.LIKE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.NOT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.NULL;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.ON;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.OR;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.ORDER;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.OUTER;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.PRIMARY;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.REFERENCES;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.RIGHT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.SELECT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.SESSION_USER;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.SOME;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.TABLE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.TABLESAMPLE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.THEN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.TO;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.UNION;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.UNIQUE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.USER;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.WHEN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.WHERE;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.WITH;

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
