package org.nkjmlab.sorm4j.mapping.extension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * Convert {@link ResultSet} from database to specified objects.
 *
 * @author nkjm
 *
 */
public interface ResultSetConverter {


  /**
   * Reads a column from the current row in the provided {@link java.sql.ResultSet} and returns an
   * instance of the specified Java {@link java.lang.Class} containing the values read.
   *
   * This method is mainly used for "SEARCH SQL AND READ TO POJO". i.e. Convert from Sql to Java by
   * the specified class.
   *
   * This method is used while converting {@link java.sql.ResultSet} rows to objects. The class type
   * is the field type in the target bean.
   *
   * null's will be respected for any non-native types. This means that if a field is of type
   * Integer it will be able to receive a null value from the ResultSet; on the other hand, if a
   * field is of type int it will receive 0 for a null value from the {@link java.sql.ResultSet}.
   *
   * @param resultSet {@link java.sql.ResultSet} (positioned in the row to be processed)
   * @param column column index in the result set (starting with 1)
   * @param setterParameterType {@link java.lang.Class} of the object to be returned
   * @throws SQLException
   *
   */
  Object getValueBySetterParameterType(ResultSet resultSet, int column,
      Class<?> setterParameterType) throws SQLException;

  /**
   * Returns the given type could be converted to the native object or not.
   *
   * @param type
   * @return
   */
  boolean isEnableToConvertNativeObject(Class<?> type);

  /**
   * Converts the result from database to a map objects. The data of the column is extracted by
   * corresponding column types.
   *
   * @param resultSet
   * @param columns
   * @param columnTypes SQL types from {@link java.sql.Types}
   * @return
   * @throws SQLException
   */
  Map<String, Object> toSingleMap(ResultSet resultSet, List<String> columns,
      List<Integer> columnTypes) throws SQLException;


  /**
   * Converts to a single native object of the given object class.
   *
   * @param <T>
   * @param resultSet
   * @param objectClass
   * @return
   * @throws SQLException
   */
  <T> T toSingleNativeObject(ResultSet resultSet, Class<T> objectClass) throws SQLException;



}
