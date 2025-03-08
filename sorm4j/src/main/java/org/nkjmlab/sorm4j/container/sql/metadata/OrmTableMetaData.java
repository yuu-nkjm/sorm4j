package org.nkjmlab.sorm4j.container.sql.metadata;

import java.util.List;

import org.nkjmlab.sorm4j.mapping.annotation.OrmColumnAliasPrefix;

public interface OrmTableMetaData {
  /**
   * Creates a list of column aliases.
   *
   * <p>For example, if the "customer" table has "id" and "name" columns, and {@link
   * OrmColumnAliasPrefix} is "c" returns <code>"customer.id as cid, customer.name as cname"</code>.
   *
   * @return column aliases (e.g. <code>"customer.id as cid, customer.name as cname"</code>)
   */
  List<String> getColumnAliases();

  List<String> getColumns();

  String getTableName();
}
