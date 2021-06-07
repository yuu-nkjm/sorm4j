package org.nkjmlab.sorm4j.sql;

import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Sql keywords wrapped with space.
 *
 * @author nkjm
 * @see <a href="https://en.wikipedia.org/wiki/SQL_reserved_words">SQL reserved words -
 *      Wikipedia</a>
 */
@Experimental
public interface SqlKeyword {

  public static final String ALL = wrapSpace("ALL").toLowerCase();
  public static final String AND = wrapSpace("AND").toLowerCase();
  public static final String ANY = wrapSpace("ANY").toLowerCase();
  public static final String AS = wrapSpace("AS").toLowerCase();
  public static final String AUTHORIZATION = wrapSpace("AUTHORIZATION").toLowerCase();
  public static final String CASE = wrapSpace("CASE").toLowerCase();
  public static final String CHECK = wrapSpace("CHECK").toLowerCase();
  public static final String COLLATE = wrapSpace("COLLATE").toLowerCase();
  public static final String COLUMN = wrapSpace("COLUMN").toLowerCase();
  public static final String CONSTRAINT = wrapSpace("CONSTRAINT").toLowerCase();
  public static final String CREATE = wrapSpace("CREATE").toLowerCase();
  public static final String CROSS = wrapSpace("CROSS").toLowerCase();
  public static final String CURRENT_DATE = wrapSpace("CURRENT_DATE").toLowerCase();
  public static final String CURRENT_TIME = wrapSpace("CURRENT_TIME").toLowerCase();
  public static final String CURRENT_TIMESTAMP = wrapSpace("CURRENT_TIMESTAMP").toLowerCase();
  public static final String CURRENT_USER = wrapSpace("CURRENT_USER").toLowerCase();
  public static final String DEFAULT = wrapSpace("DEFAULT").toLowerCase();
  public static final String DISTINCT = wrapSpace("DISTINCT").toLowerCase();
  public static final String ELSE = wrapSpace("ELSE").toLowerCase();
  public static final String END = wrapSpace("END").toLowerCase();
  public static final String EXCEPT = wrapSpace("EXCEPT").toLowerCase();
  public static final String FETCH = wrapSpace("FETCH").toLowerCase();
  public static final String FOR = wrapSpace("FOR").toLowerCase();
  public static final String FOREIGN = wrapSpace("FOREIGN").toLowerCase();
  public static final String FROM = wrapSpace("FROM").toLowerCase();
  public static final String FULL = wrapSpace("FULL").toLowerCase();
  public static final String GRANT = wrapSpace("GRANT").toLowerCase();
  public static final String GROUP = wrapSpace("GROUP").toLowerCase();
  public static final String HAVING = wrapSpace("HAVING").toLowerCase();
  public static final String IN = wrapSpace("IN").toLowerCase();
  public static final String INNER = wrapSpace("INNER").toLowerCase();
  public static final String INTERSECT = wrapSpace("INTERSECT").toLowerCase();
  public static final String INTO = wrapSpace("INTO").toLowerCase();
  public static final String IS = wrapSpace("IS").toLowerCase();
  public static final String JOIN = wrapSpace("JOIN").toLowerCase();
  public static final String LEFT = wrapSpace("LEFT").toLowerCase();
  public static final String LIKE = wrapSpace("LIKE").toLowerCase();
  public static final String NOT = wrapSpace("NOT").toLowerCase();
  public static final String NULL = wrapSpace("NULL").toLowerCase();
  public static final String ON = wrapSpace("ON").toLowerCase();
  public static final String OR = wrapSpace("OR").toLowerCase();
  public static final String ORDER = wrapSpace("ORDER").toLowerCase();
  public static final String OUTER = wrapSpace("OUTER").toLowerCase();
  public static final String PRIMARY = wrapSpace("PRIMARY").toLowerCase();
  public static final String REFERENCES = wrapSpace("REFERENCES").toLowerCase();
  public static final String RIGHT = wrapSpace("RIGHT").toLowerCase();
  public static final String SELECT = wrapSpace("SELECT").toLowerCase();
  public static final String SESSION_USER = wrapSpace("SESSION_USER").toLowerCase();
  public static final String SOME = wrapSpace("SOME").toLowerCase();
  public static final String TABLE = wrapSpace("TABLE").toLowerCase();
  public static final String TABLESAMPLE = wrapSpace("TABLESAMPLE").toLowerCase();
  public static final String THEN = wrapSpace("THEN").toLowerCase();
  public static final String TO = wrapSpace("TO").toLowerCase();
  public static final String UNION = wrapSpace("UNION").toLowerCase();
  public static final String UNIQUE = wrapSpace("UNIQUE").toLowerCase();
  public static final String USER = wrapSpace("USER").toLowerCase();
  public static final String WHEN = wrapSpace("WHEN").toLowerCase();
  public static final String WHERE = wrapSpace("WHERE").toLowerCase();
  public static final String WITH = wrapSpace("WITH").toLowerCase();


  public static final String STAR = wrapSpace("*").toLowerCase();
  public static final String SELECT_STAR = wrapSpace("SELECT *").toLowerCase();
  public static final String ASC = wrapSpace("ASC").toLowerCase();
  public static final String BETWEEN = wrapSpace("BETWEEN").toLowerCase();
  public static final String CAST = wrapSpace("CAST").toLowerCase();
  public static final String COUNT = wrapSpace("COUNT").toLowerCase();
  public static final String DESC = wrapSpace("DESC").toLowerCase();
  public static final String GROUP_BY = wrapSpace("GROUP BY").toLowerCase();
  public static final String LIMIT = wrapSpace("LIMIT").toLowerCase();
  public static final String MAX = wrapSpace("MAX").toLowerCase();
  public static final String MIN = wrapSpace("MIN").toLowerCase();
  public static final String ORDER_BY = wrapSpace("ORDER BY").toLowerCase();
  public static final String SUM = wrapSpace("SUM").toLowerCase();


  private static String wrapSpace(String str) {
    return " " + str + " ";
  }


}
