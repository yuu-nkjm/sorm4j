package org.nkjmlab.sorm4j.common;

import java.util.List;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;

/**
 * An instance of this class contains table metadata.
 *
 * @author yuu_nkjm
 *
 */
public interface TableMetaData extends JdbcTableMetaData {


  /**
   * <p>
   * Creates a list of column aliases.
   *
   * <p>
   * For example, if the "customer" table has "id" and "name" columns, and
   * {@link OrmColumnAliasPrefix} is "c" returns
   * <code>"customer.id as cid, customer.name as cname"</code>.
   *
   * @return column aliases (e.g. <code>"customer.id as cid, customer.name as cname"</code>)
   */

  List<String> getColumnAliases();

}
