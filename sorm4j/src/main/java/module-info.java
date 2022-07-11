module org.nkjmlab.sorm4j {
  requires transitive java.sql;
  requires transitive org.apache.logging.log4j;
  requires transitive org.slf4j;
  requires transitive com.fasterxml.jackson.databind;
  requires transitive com.fasterxml.jackson.annotation;

  exports org.nkjmlab.sorm4j;
  exports org.nkjmlab.sorm4j.annotation;
  exports org.nkjmlab.sorm4j.common;
  exports org.nkjmlab.sorm4j.context;
  exports org.nkjmlab.sorm4j.mapping;
  exports org.nkjmlab.sorm4j.result;
  exports org.nkjmlab.sorm4j.sql;
  exports org.nkjmlab.sorm4j.table;
  exports org.nkjmlab.sorm4j.util.command;
  exports org.nkjmlab.sorm4j.util.h2;
  exports org.nkjmlab.sorm4j.util.h2.sql;
  exports org.nkjmlab.sorm4j.util.h2.sql.annotation;
  exports org.nkjmlab.sorm4j.util.jackson;
  exports org.nkjmlab.sorm4j.util.json;
  exports org.nkjmlab.sorm4j.util.logger;
  exports org.nkjmlab.sorm4j.util.sql;
  exports org.nkjmlab.sorm4j.util.table_def;
  exports org.nkjmlab.sorm4j.util.table_def.annotation;
}
