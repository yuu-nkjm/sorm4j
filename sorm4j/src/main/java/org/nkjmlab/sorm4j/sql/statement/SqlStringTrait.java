package org.nkjmlab.sorm4j.sql.statement;

/** Trait for SQL literal formatting and quoting. */
public interface SqlStringTrait {

  default String literal(Object element) {
    return SqlStringUtils.literal(element);
  }

  default String quote(String str) {
    return SqlStringUtils.quote(str);
  }

  default String statement(Object... elements) {
    return SqlStringUtils.join(" ", elements);
  }

  default String escapeJavaString(String str) {
    return SqlStringUtils.escapeJavaString(str);
  }

  default String chars(int num) {
    return SqlStringUtils.chars(num);
  }

  default String decimal(int precision) {
    return SqlStringUtils.decimal(precision);
  }

  default String decimal(int precision, int scale) {
    return SqlStringUtils.decimal(precision, scale);
  }
}
