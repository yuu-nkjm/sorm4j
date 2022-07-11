package org.nkjmlab.sorm4j.util.jackson;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.datatype.OrmJsonColumnContainer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Experimental
public class JacksonColumnValueToJavaObjectConverter implements ColumnValueToJavaObjectConverter {

  private final ObjectMapper objectMapper;

  private final Map<Class<?>, Boolean> ormJsonContainer = new ConcurrentHashMap<>();

  public JacksonColumnValueToJavaObjectConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private boolean isOrmJsonContainer(Class<?> type) {
    return ormJsonContainer.computeIfAbsent(type,
        key -> ArrayUtils.getInternalComponentType(type)
            .getAnnotation(OrmJsonColumnContainer.class) != null || List.class.isAssignableFrom(type)
            || Map.class.isAssignableFrom(type));
  }

  @Override
  public boolean test(Class<?> toType) {
    return isOrmJsonContainer(toType);
  }

  @Override
  public Object convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
      throws SQLException {
    try {
      return objectMapper.readValue(resultSet.getBytes(columnIndex), toType);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }
}
