package org.nkjmlab.sorm4j.extension.h2.sql.statement;

import org.nkjmlab.sorm4j.common.annotation.Experimental;

@Deprecated
@Experimental
public class InsertSelectCsvReadSql {

  private final String sql;

  private InsertSelectCsvReadSql(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(String tableName, SelectCsvReadSql selectCsvRead) {
    return new Builder(tableName, selectCsvRead);
  }

  public static class Builder {

    private SelectCsvReadSql selectCsvRead;
    private String tableName;

    private Builder() {}

    public Builder(String tableName, SelectCsvReadSql selectCsvRead) {
      this.tableName = tableName;
      this.selectCsvRead = selectCsvRead;
    }

    public InsertSelectCsvReadSql build() {
      return new InsertSelectCsvReadSql(
          "insert into "
              + tableName
              + "("
              + String.join(",", selectCsvRead.getSelectColumns())
              + ") "
              + selectCsvRead.getSql());
    }
  }
}
