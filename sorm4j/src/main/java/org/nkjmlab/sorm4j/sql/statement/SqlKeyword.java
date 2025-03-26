package org.nkjmlab.sorm4j.sql.statement;

/**
 * Sql keywords wrapped with space.
 *
 * @author nkjm
 * @see <a href="https://en.wikipedia.org/wiki/SQL_reserved_words">SQL reserved words -
 *     Wikipedia</a>
 */
public interface SqlKeyword {

  public static final String ALL = wrapSpace("all");
  public static final String AND = wrapSpace("and");
  public static final String ANY = wrapSpace("any");
  public static final String AS = wrapSpace("as");
  public static final String AUTHORIZATION = wrapSpace("authorization");
  public static final String CASE = wrapSpace("case");
  public static final String CHECK = wrapSpace("check");
  public static final String COLLATE = wrapSpace("collate");
  public static final String COLUMN = wrapSpace("column");
  public static final String CONSTRAINT = wrapSpace("constraint");
  public static final String CREATE = wrapSpace("create");
  public static final String CROSS = wrapSpace("cross");
  public static final String CURRENT_DATE = wrapSpace("current_date");
  public static final String CURRENT_TIME = wrapSpace("current_time");
  public static final String CURRENT_TIMESTAMP = wrapSpace("current_timestamp");
  public static final String CURRENT_USER = wrapSpace("current_user");
  public static final String DEFAULT = wrapSpace("default");
  public static final String DISTINCT = wrapSpace("distinct");
  public static final String ELSE = wrapSpace("else");
  public static final String END = wrapSpace("end");
  public static final String EXCEPT = wrapSpace("except");
  public static final String FETCH = wrapSpace("fetch");
  public static final String FOR = wrapSpace("for");
  public static final String FOREIGN = wrapSpace("foreign");
  public static final String FROM = wrapSpace("from");
  public static final String FULL = wrapSpace("full");
  public static final String GRANT = wrapSpace("grant");
  public static final String GROUP = wrapSpace("group");
  public static final String HAVING = wrapSpace("having");
  public static final String IN = wrapSpace("in");
  public static final String INNER = wrapSpace("inner");
  public static final String INTERSECT = wrapSpace("intersect");
  public static final String INTO = wrapSpace("into");
  public static final String IS = wrapSpace("is");
  public static final String JOIN = wrapSpace("join");
  public static final String LEFT = wrapSpace("left");
  public static final String LIKE = wrapSpace("like");
  public static final String NOT = wrapSpace("not");
  public static final String NULL = wrapSpace("null");
  public static final String ON = wrapSpace("on");
  public static final String OR = wrapSpace("or");
  public static final String ORDER = wrapSpace("order");
  public static final String OUTER = wrapSpace("outer");
  public static final String PRIMARY = wrapSpace("primary");
  public static final String REFERENCES = wrapSpace("references");
  public static final String RIGHT = wrapSpace("right");
  public static final String SELECT = wrapSpace("select");
  public static final String SESSION_USER = wrapSpace("session_user");
  public static final String SOME = wrapSpace("some");
  public static final String TABLE = wrapSpace("table");
  public static final String TABLESAMPLE = wrapSpace("tablesample");
  public static final String THEN = wrapSpace("then");
  public static final String TO = wrapSpace("to");
  public static final String UNION = wrapSpace("union");
  public static final String UNIQUE = wrapSpace("unique");
  public static final String USER = wrapSpace("user");
  public static final String WHEN = wrapSpace("when");
  public static final String WHERE = wrapSpace("where");
  public static final String WITH = wrapSpace("with");

  public static final String ASC = wrapSpace("asc");
  public static final String BETWEEN = wrapSpace("between");
  public static final String CAST = wrapSpace("cast");
  public static final String DESC = wrapSpace("desc");
  public static final String GROUP_BY = wrapSpace("group by");
  public static final String LIMIT = wrapSpace("limit");
  public static final String MAX = wrapSpace("max");
  public static final String MIN = wrapSpace("min");
  public static final String ORDER_BY = wrapSpace("order by");
  public static final String COUNT = wrapSpace("count");
  public static final String AVG = wrapSpace("avg");
  public static final String SUM = wrapSpace("sum");
  public static final String IS_NULL = wrapSpace("is null");
  public static final String IS_NOT_NULL = wrapSpace("is not null");

  /** Data type * */
  public static final String ARRAY = wrapSpace("array");

  public static final String BIGINT = wrapSpace("bigint");
  public static final String BOOLEAN = wrapSpace("boolean");
  public static final String CHAR = wrapSpace("char");
  public static final String DATE = wrapSpace("date");
  public static final String DECIMAL = wrapSpace("decimal");
  public static final String DOUBLE = wrapSpace("double");
  public static final String IDENTITY = wrapSpace("identity");
  public static final String INT = wrapSpace("int");
  public static final String REAL = wrapSpace("real");
  public static final String SMALLINT = wrapSpace("smallint");
  public static final String TIME = wrapSpace("time");
  public static final String TIMESTAMP = wrapSpace("timestamp");
  public static final String TINYINT = wrapSpace("tinyint");
  public static final String VARCHAR = wrapSpace("varchar");

  public static final String AUTO_INCREMENT = wrapSpace("auto_increment");
  public static final String NOT_NULL = wrapSpace("not null");
  public static final String PRIMARY_KEY = wrapSpace("primary key");

  /** util * */
  public static final String STAR = wrapSpace("*");

  public static final String SELECT_STAR = wrapSpace("select *");

  private static String wrapSpace(String str) {
    return str == null ? null : " " + str + " ";
  }
}
