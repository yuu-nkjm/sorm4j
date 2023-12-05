package org.nkjmlab.sorm4j.util.table_def;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface WithTableDefinition {

  /**
   * Gets {@link Orm} object
   *
   * @return
   */
  Orm getOrm();

  /**
   * Gets the table definition.
   *
   * @return
   */
  TableDefinition getTableDefinition();

  default WithTableDefinition createTableIfNotExists() {
    getTableDefinition().createTableIfNotExists(getOrm());
    return this;
  }

  default WithTableDefinition createIndexesIfNotExists() {
    getTableDefinition().createIndexesIfNotExists(getOrm());
    return this;
  }

  default WithTableDefinition dropTableIfExists() {
    getTableDefinition().dropTableIfExists(getOrm());
    return this;
  }

  default WithTableDefinition dropTableIfExistsCascade() {
    getTableDefinition().dropTableIfExistsCascade(getOrm());
    return this;
  }
}
