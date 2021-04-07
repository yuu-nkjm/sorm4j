package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.List;

/**
 * Mapping {@link ResultSet} to object.
 *
 * @author nkjm
 *
 */
public interface ResultSetMapper {

  /**
   * Maps the current row in the given resultSet to an object.
   *
   * @param <T>
   * @param objectClass
   * @param resultSet
   * @return
   */
  <T> T mapRow(Class<T> objectClass, ResultSet resultSet);


  /**
   * Maps the all rows in the given resultSet to an object list.
   *
   * @param <T>
   * @param objectClass
   * @param resultSet
   * @return
   */
  <T> List<T> mapRowList(Class<T> objectClass, ResultSet resultSet);

}
