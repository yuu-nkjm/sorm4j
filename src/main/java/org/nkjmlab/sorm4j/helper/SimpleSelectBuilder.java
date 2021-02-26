package org.nkjmlab.sorm4j.helper;

/**
 * Example:
 * <p>
 * from: select("*").from("customers").where("id=1") .orderBy("id DESC").limit(1);
 * </p>
 * <p>
 * to: SELECT * FROM customers WHERE id=1 ORDER BY id DESC LIMIT 1
 * </p>
 */
public final class SimpleSelectBuilder {

  private String select;
  private String from;
  private String where;
  private String orderBy;
  private Integer limit;

  public SimpleSelectBuilder select(String select) {
    this.select = select;
    return this;
  }

  public SimpleSelectBuilder from(String from) {
    this.from = from;
    return this;

  }

  public SimpleSelectBuilder where(String where) {
    this.where = where;
    return this;
  }

  public SimpleSelectBuilder orderBy(String orderBy) {
    this.orderBy = orderBy;
    return this;
  }

  public SimpleSelectBuilder limit(int limit) {
    this.limit = limit;
    return this;
  }

  @Override
  public String toString() {
    return build();
  }

  public String build() {
    return "SELECT " + select + " FROM " + from + (where == null ? "" : " WHERE " + where)
        + (orderBy == null ? "" : " ORDER BY " + orderBy)
        + (limit == null ? "" : " LIMIT " + limit);
  }
}
