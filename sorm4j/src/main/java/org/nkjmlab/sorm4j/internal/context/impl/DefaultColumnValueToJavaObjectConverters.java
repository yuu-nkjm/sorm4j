package org.nkjmlab.sorm4j.internal.context.impl;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.util.JdbcTypeUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Default implementation of {@link ColumnValueToJavaObjectConverters}
 *
 * @author nkjm
 */
public final class DefaultColumnValueToJavaObjectConverters
    implements ColumnValueToJavaObjectConverters {

  private final Map<Class<?>, Optional<ColumnValueToJavaObjectConverter>> converterHitCache;
  private final List<ColumnValueToJavaObjectConverter> converters;
  private final Map<Class<?>, Boolean> supportedValueTypeCache = new ConcurrentHashMap<>();
  private final ColumnValueToJavaObjectConverter defaultConverter =
      new DefaultColumnValueToJavaObjectConverter();

  @Override
  public boolean isSupportedComponentType(Class<?> objectClass) {
    return supportedValueTypeCache.computeIfAbsent(
        objectClass,
        key -> getHitConverter(objectClass).isPresent() || defaultConverter.test(objectClass));
  }

  /**
   * @param converters the converter which corresponding to the key class is applied to the column
   *     value.
   */
  public DefaultColumnValueToJavaObjectConverters(ColumnValueToJavaObjectConverter... converters) {
    this.converters = Arrays.asList(converters);
    this.converterHitCache =
        this.converters.isEmpty() ? Collections.emptyMap() : new ConcurrentHashMap<>();
  }

  @Override
  public <T> T convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<T> toType) {
    try {
      final Optional<ColumnValueToJavaObjectConverter> _converter = getHitConverter(toType);
      return _converter.isPresent()
          ? (T) _converter.get().convertTo(resultSet, columnIndex, columnType, toType)
          : (T) defaultConverter.convertTo(resultSet, columnIndex, columnType, toType);
    } catch (Exception e) {
      String tableName =
          Try.getOrElse(() -> resultSet.getMetaData().getTableName(columnIndex), "UNKNOWN_TABLE");
      String columnLabel =
          Try.getOrElse(
              () -> resultSet.getMetaData().getColumnLabel(columnIndex), "UNKNOWN_COLUMN");
      Object[] params = {
        tableName, columnLabel, columnIndex, JdbcTypeUtils.convert(columnType), toType
      };
      throw new SormException(
          ParameterizedStringFormatter.LENGTH_256.format(
              "tableName=[{}], columnLabel=[{}], columnIndex=[{}], columnType=[{}], toType=[{}]",
              params),
          e);
    }
  }

  private Optional<ColumnValueToJavaObjectConverter> getHitConverter(Class<?> toType) {
    return converters.isEmpty()
        ? Optional.empty()
        : converterHitCache.computeIfAbsent(
            toType, key -> converters.stream().filter(conv -> conv.test(toType)).findAny());
  }
}
