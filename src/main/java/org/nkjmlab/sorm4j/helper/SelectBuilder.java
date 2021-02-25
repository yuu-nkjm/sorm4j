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
public class SelectBuilder {

  private String select;
  private String from;
  private String where;
  private String orderBy;
  private Integer limit;

  public SelectBuilder select(String select) {
    this.select = select;
    return this;
  }

  public SelectBuilder from(String from) {
    this.from = from;
    return this;

  }

  public SelectBuilder where(String where) {
    this.where = where;
    return this;
  }

  public SelectBuilder orderBy(String orderBy) {
    this.orderBy = orderBy;
    return this;
  }

  public SelectBuilder limit(int limit) {
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
