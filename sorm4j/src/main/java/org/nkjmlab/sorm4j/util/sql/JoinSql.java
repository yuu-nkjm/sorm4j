package org.nkjmlab.sorm4j.util.sql;

import java.util.ArrayList;
import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.table.TableMappedOrm;
import org.nkjmlab.sorm4j.util.sql.SelectSql.Condition;

@Experimental
public class JoinSql {

  private JoinSql() {}

  @Experimental
  public static JoinSql.Builder builder(TableMappedOrm<?> firstTable) {
    return new Builder(firstTable);
  }

  public static JoinSql.Builder builder(TableMetaData firstTable) {
    return new Builder(firstTable);
  }

  public static class Builder {
    private final List<String> columns = new ArrayList<>();
    private final List<String> froms = new ArrayList<>();

    private boolean distinct;
    private String where;
    private String orderBy;
    private String limit;

    private Builder(TableMetaData tableMetaData) {
      this.columns.addAll(tableMetaData.getColumnAliases());
      this.froms.add(tableMetaData.getTableName());
    }

    private Builder(TableMappedOrm<?> first) {
      this(first.getTableMetaData());
    }

    public JoinSql.Builder joinUsing(TableMetaData other, String... columnsForJoin) {
      return joinUsing("join", other, columnsForJoin);
    }

    public JoinSql.Builder joinUsing(TableMappedOrm<?> other, String... columnsForJoin) {
      return joinUsing(other.getTableMetaData(), columnsForJoin);
    }

    public JoinSql.Builder leftJoinUsing(TableMetaData other, String... columnsForJoin) {
      return joinUsing("left join", other, columnsForJoin);
    }

    public JoinSql.Builder leftJoinUsing(TableMappedOrm<?> other, String... columnsForJoin) {
      return leftJoinUsing(other.getTableMetaData(), columnsForJoin);
    }

    public JoinSql.Builder joinOn(TableMetaData other, String onCondition) {
      return joinOn("join", other, onCondition);
    }

    public JoinSql.Builder joinOn(TableMappedOrm<?> other, String onCondition) {
      return joinOn(other.getTableMetaData(), onCondition);
    }

    public JoinSql.Builder leftJoinOn(TableMetaData other, String onCondition) {
      return joinOn("left join", other, onCondition);
    }

    public JoinSql.Builder leftJoinOn(TableMappedOrm<?> other, String onCondition) {
      return leftJoinOn(other.getTableMetaData(), onCondition);
    }

    private JoinSql.Builder joinUsing(
        String joinType, TableMetaData tableMetaData, String... columnsForJoin) {
      return join(joinType, tableMetaData, "using (" + String.join(",", columnsForJoin) + ")");
    }

    private JoinSql.Builder joinOn(
        String joinType, TableMetaData tableMetaData, String onCondition) {
      return join(joinType, tableMetaData, "on " + onCondition);
    }

    public JoinSql.Builder join(
        String joinType, TableMetaData otherTableMetaData, String joinCondition) {
      columns.addAll(otherTableMetaData.getColumnAliases());
      froms.add(String.join(" ", joinType, otherTableMetaData.getTableName(), joinCondition));
      return this;
    }

    public JoinSql.Builder join(String joinType, TableMappedOrm<?> other, String joinCondition) {
      return join(joinType, other.getTableMetaData(), joinCondition);
    }

    /**
     * Creates where clause.
     *
     * @param condition
     * @return
     */
    public Builder where(Condition condition) {
      where(condition.toString());
      return this;
    }

    /**
     * Creates where clause.
     *
     * @param expr
     * @return
     */
    public Builder where(String expr) {
      where = expr;
      return this;
    }

    /**
     * Creates order by clause.
     *
     * @see <a href="http://www.h2database.com/html/grammar.html#select_order">SQL Grammar</a>
     * @param order
     * @return
     */
    public Builder orderBy(String... order) {
      orderBy = String.join(" ", order);
      return this;
    }

    /**
     * Creates limit clause.
     *
     * @param limit
     * @return
     */
    public Builder limit(int limit) {
      return limit(limit, 0);
    }

    /**
     * Creates limit clause with offset.
     *
     * @param limit
     * @return
     */
    public Builder limit(int limit, int offset) {
      this.limit = limit + (offset > 0 ? " offset " + offset : "");
      return this;
    }

    public String build() {

      StringBuilder sql = new StringBuilder("select ");
      if (distinct) {
        sql.append("distinct ");
      }
      sql.append(String.join(",", columns.stream().toArray(String[]::new)) + " ");
      sql.append("from " + String.join(" ", froms) + " ");
      if (where != null) {
        sql.append("where " + where + " ");
      }
      if (orderBy != null) {
        sql.append("order by " + orderBy + " ");
      }
      if (limit != null) {
        sql.append("limit " + limit);
      }
      return sql.toString();
    }
  }
}
