package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface ResultSetMapper {

  <T> T mapRow(Class<T> objectClass, ResultSet resultSet);

  Map<String, Object> mapRow(ResultSet resultSet);

  <T> List<T> mapRows(Class<T> objectClass, ResultSet resultSet);

  List<Map<String, Object>> mapRows(ResultSet resultSet);

}
