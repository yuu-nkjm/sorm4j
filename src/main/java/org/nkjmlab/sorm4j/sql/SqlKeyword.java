package org.nkjmlab.sorm4j.sql;

import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 *
 * @author nkjm
 * @see <a href="https://en.wikipedia.org/wiki/SQL_reserved_words">SQL reserved words -
 *      Wikipedia</a>
 */
@Experimental
public interface SqlKeyword {

  public static final String ALL = wrapSpace("ALL");
  public static final String AND = wrapSpace("AND");
  public static final String ANY = wrapSpace("ANY");
  public static final String AS = wrapSpace("AS");
  public static final String AUTHORIZATION = wrapSpace("AUTHORIZATION");
  public static final String CASE = wrapSpace("CASE");
  public static final String CHECK = wrapSpace("CHECK");
  public static final String COLLATE = wrapSpace("COLLATE");
  public static final String COLUMN = wrapSpace("COLUMN");
  public static final String CONSTRAINT = wrapSpace("CONSTRAINT");
  public static final String CREATE = wrapSpace("CREATE");
  public static final String CROSS = wrapSpace("CROSS");
  public static final String CURRENT_DATE = wrapSpace("CURRENT_DATE");
  public static final String CURRENT_TIME = wrapSpace("CURRENT_TIME");
  public static final String CURRENT_TIMESTAMP = wrapSpace("CURRENT_TIMESTAMP");
  public static final String CURRENT_USER = wrapSpace("CURRENT_USER");
  public static final String DEFAULT = wrapSpace("DEFAULT");
  public static final String DISTINCT = wrapSpace("DISTINCT");
  public static final String ELSE = wrapSpace("ELSE");
  public static final String END = wrapSpace("END");
  public static final String EXCEPT = wrapSpace("EXCEPT");
  public static final String FETCH = wrapSpace("FETCH");
  public static final String FOR = wrapSpace("FOR");
  public static final String FOREIGN = wrapSpace("FOREIGN");
  public static final String FROM = wrapSpace("FROM");
  public static final String FULL = wrapSpace("FULL");
  public static final String GRANT = wrapSpace("GRANT");
  public static final String GROUP = wrapSpace("GROUP");
  public static final String HAVING = wrapSpace("HAVING");
  public static final String IN = wrapSpace("IN");
  public static final String INNER = wrapSpace("INNER");
  public static final String INTERSECT = wrapSpace("INTERSECT");
  public static final String INTO = wrapSpace("INTO");
  public static final String IS = wrapSpace("IS");
  public static final String JOIN = wrapSpace("JOIN");
  public static final String LEFT = wrapSpace("LEFT");
  public static final String LIKE = wrapSpace("LIKE");
  public static final String NOT = wrapSpace("NOT");
  public static final String NULL = wrapSpace("NULL");
  public static final String ON = wrapSpace("ON");
  public static final String OR = wrapSpace("OR");
  public static final String ORDER = wrapSpace("ORDER");
  public static final String OUTER = wrapSpace("OUTER");
  public static final String PRIMARY = wrapSpace("PRIMARY");
  public static final String REFERENCES = wrapSpace("REFERENCES");
  public static final String RIGHT = wrapSpace("RIGHT");
  public static final String SELECT = wrapSpace("SELECT");
  public static final String SESSION_USER = wrapSpace("SESSION_USER");
  public static final String SOME = wrapSpace("SOME");
  public static final String TABLE = wrapSpace("TABLE");
  public static final String TABLESAMPLE = wrapSpace("TABLESAMPLE");
  public static final String THEN = wrapSpace("THEN");
  public static final String TO = wrapSpace("TO");
  public static final String UNION = wrapSpace("UNION");
  public static final String UNIQUE = wrapSpace("UNIQUE");
  public static final String USER = wrapSpace("USER");
  public static final String WHEN = wrapSpace("WHEN");
  public static final String WHERE = wrapSpace("WHERE");
  public static final String WITH = wrapSpace("WITH");

  static String wrapSpace(String str) {
    return " " + str + " ";
  }


}
