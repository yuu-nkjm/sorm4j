package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.List;

/**
 * Mapping {@link ResultSet} to object.
 *
 * @author nkjm
 *
 */
public interface TypedResultSetMapper<T> {

  /**
   * Maps the current row in the given resultSet to an object.
   *
   * @param resultSet
   * @return
   */
  T mapRow(ResultSet resultSet);


  /**
   * Maps the all rows in the given resultSet to an object list.
   *
   * @param resultSet
   * @return
   */
  List<T> mapRowList(ResultSet resultSet);

}
