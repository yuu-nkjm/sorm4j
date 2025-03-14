package org.nkjmlab.sorm4j.sql.statement;

import java.util.ArrayList;
import java.util.List;

import org.nkjmlab.sorm4j.common.annotation.Internal;
import org.nkjmlab.sorm4j.sql.metadata.OrmTableMetaData;
import org.nkjmlab.sorm4j.table.orm.TableOrm;

public class JoinSql {

  private JoinSql() {}

  public static JoinSql.Builder builder(TableOrm<?> firstTable) {
    return new Builder(firstTable.getOrmTableMetaData());
  }

  @Internal
  public static JoinSql.Builder builder(OrmTableMetaData firstTable) {
    return new Builder(firstTable);
  }

  public static class Builder {
    private final List<String> columns = new ArrayList<>();
    private final List<String> froms = new ArrayList<>();

    private boolean distinct;
    private String where;
    private String orderBy;
    private String limit;

    private Builder(OrmTableMetaData tableMetaData) {
      this.columns.addAll(tableMetaData.getColumnAliases());
      this.froms.add(tableMetaData.getTableName());
    }

    public Builder distinct() {
      distinct = true;
      return this;
    }

    public JoinSql.Builder joinUsing(OrmTableMetaData other, String... columnsForJoin) {
      return joinUsing("join", other, columnsForJoin);
    }

    public JoinSql.Builder joinUsing(TableOrm<?> other, String... columnsForJoin) {
      return joinUsing(other.getOrmTableMetaData(), columnsForJoin);
    }

    public JoinSql.Builder leftJoinUsing(OrmTableMetaData other, String... columnsForJoin) {
      return joinUsing("left join", other, columnsForJoin);
    }

    public JoinSql.Builder leftJoinUsing(TableOrm<?> other, String... columnsForJoin) {
      return leftJoinUsing(other.getOrmTableMetaData(), columnsForJoin);
    }

    public JoinSql.Builder joinOn(OrmTableMetaData other, String onCondition) {
      return joinOn("join", other, onCondition);
    }

    public JoinSql.Builder joinOn(TableOrm<?> other, String onCondition) {
      return joinOn(other.getOrmTableMetaData(), onCondition);
    }

    public JoinSql.Builder leftJoinOn(OrmTableMetaData other, String onCondition) {
      return joinOn("left join", other, onCondition);
    }

    public JoinSql.Builder leftJoinOn(TableOrm<?> other, String onCondition) {
      return leftJoinOn(other.getOrmTableMetaData(), onCondition);
    }

    private JoinSql.Builder joinUsing(
        String joinType, OrmTableMetaData tableMetaData, String... columnsForJoin) {
      return join(joinType, tableMetaData, "using (" + String.join(",", columnsForJoin) + ")");
    }

    private JoinSql.Builder joinOn(
        String joinType, OrmTableMetaData tableMetaData, String onCondition) {
      return join(joinType, tableMetaData, "on " + onCondition);
    }

    public JoinSql.Builder join(
        String joinType, OrmTableMetaData otherTableMetaData, String joinCondition) {
      columns.addAll(otherTableMetaData.getColumnAliases());
      froms.add(String.join(" ", joinType, otherTableMetaData.getTableName(), joinCondition));
      return this;
    }

    public JoinSql.Builder join(String joinType, TableOrm<?> other, String joinCondition) {
      return join(joinType, other.getOrmTableMetaData(), joinCondition);
    }

    /**
     * Creates where clause.
     *
     * @param condition
     * @return
     */
    public Builder where(ConditionSql condition) {
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

      StringBuilder sql = new StringBuilder("select");
      if (distinct) {
        sql.append(" distinct");
      }
      sql.append(" " + String.join(",", columns.stream().toArray(String[]::new)));
      sql.append(" from " + String.join(" ", froms));
      if (where != null) {
        sql.append(" where " + where);
      }
      if (orderBy != null) {
        sql.append(" order by " + orderBy);
      }
      if (limit != null) {
        sql.append(" limit " + limit);
      }
      return sql.toString();
    }
  }
}
