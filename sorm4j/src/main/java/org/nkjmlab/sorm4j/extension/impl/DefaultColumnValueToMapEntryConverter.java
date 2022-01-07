package org.nkjmlab.sorm4j.extension.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.ColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.extension.ColumnValueToMapKeyConverter;
import org.nkjmlab.sorm4j.extension.ColumnValueToMapValueConverter;

/**
 * Default implementation of {@link ColumnValueToJavaObjectConverters}
 *
 * @author nkjm
 *
 */

public final class DefaultColumnValueToMapEntryConverter implements ColumnValueToMapEntryConverter {

  public enum LetterCaseOfKey {
    LOWER_CASE, UPPER_CASE, CANONICAL_CASE, NO_CONVERSION;
  }

  private final ColumnValueToMapKeyConverter keyConverter;
  private final ColumnValueToMapValueConverter valueConverter;

  public DefaultColumnValueToMapEntryConverter() {
    this(LetterCaseOfKey.LOWER_CASE, Collections.emptyMap());
  }

  public DefaultColumnValueToMapEntryConverter(LetterCaseOfKey letterCaseOfKey,
      Map<Integer, ColumnValueToMapValueConverter> converters) {
    this.keyConverter = new DefaultColumnValueToMapKeyConverter(letterCaseOfKey);
    this.valueConverter = new DefaultColumnValueToMapValueConverter(converters);
  }

  @Override
  public Object convertToValue(ResultSet resultSet, int column, int sqlType) throws SQLException {
    return valueConverter.convertToValue(resultSet, column, sqlType);
  }


  @Override
  public String convertToKey(String columnName) {
    return keyConverter.convertToKey(columnName);
  }

}
