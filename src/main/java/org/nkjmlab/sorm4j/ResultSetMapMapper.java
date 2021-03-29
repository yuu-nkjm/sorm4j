package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * Mapping {@link ResultSet} to {@link Map}
 *
 * @author nkjm
 *
 */
public interface ResultSetMapMapper {

  /**
   * Maps the current row in the given resultSet to an Map object.
   *
   * @param resultSet
   * @return
   */
  Map<String, Object> mapRowToMap(ResultSet resultSet);

  /**
   * Maps the all rows in the given resultSet to a map list.
   *
   * @param resultSet
   * @return
   */
  List<Map<String, Object>> mapRowsToMapList(ResultSet resultSet);

}
