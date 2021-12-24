package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.impl.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Holds mapping data from a given class and a table. The object reads a query result in
 * {@link ResultSet} via {@link DefaultResultSetConverter}.
 *
 * @author nkjm
 *
 * @param <T>
 */
public final class ColumnsMapping<T> extends Mapping<T> {

  private final PojoCreator<T> pojoCreator;

  public ColumnsMapping(SormOptions options, ResultSetConverter resultSetConverter,
      Class<T> objectClass, ColumnToAccessorMap columnToAccessorMap) {
    super(options, resultSetConverter, objectClass, columnToAccessorMap);

    Constructor<T> ormConstructor = getOrmConstructor(objectClass);

    // objectClass.isRecord() ? createRecordPojoCreator(objectClass)

    Constructor<T> ormRecordConstructor = getOrmRecordConstructor(objectClass);
    // objectClass.isRecord() ? createRecordPojoCreator(objectClass)
    this.pojoCreator =
        ormRecordConstructor != null ? createRecordPojoCreator(objectClass, ormRecordConstructor)
            : (ormConstructor != null ? createOrmConstructorPojoCreator(objectClass, ormConstructor)
                : new SetterPojoCreator<>(columnToAccessorMap, getDefaultConstructor(objectClass)));

  }

  private PojoCreator<T> createRecordPojoCreator(Class<T> objectClass, Constructor<T> constructor) {
    String[] parameterNames =
        Arrays.stream(objectClass.getDeclaredFields()).map(f -> f.getName()).toArray(String[]::new);
    return new ConstructorPojoCreator<>(getColumnToAccessorMap(), constructor, parameterNames);
  }

  private Constructor<T> getOrmRecordConstructor(Class<T> objectClass) {
    OrmRecord a = objectClass.getAnnotation(OrmRecord.class);
    if (a == null) {
      return null;
    }
    return Try.getOrElseThrow(
        () -> objectClass.getConstructor(Arrays.stream(objectClass.getDeclaredFields())
            .map(f -> f.getType()).toArray(Class[]::new)),
        e -> new SormException(
            newString("The given container class [{}] should have the canonical constructor.",
                objectClass),
            e));
  }
  // private PojoCreator<T> createRecordPojoCreator(Class<T> objectClass) {
  // Constructor<T> constructor = Try.getOrElseThrow(
  // () -> objectClass.getConstructor(Arrays.stream(objectClass.getRecordComponents())
  // .map(cn -> cn.getType()).toArray(Class[]::new)),
  // e -> new SormException(format(
  // "The given container record class [{}] does not have a valid constructor for mapping.",
  // objectClass), e));
  // String[] parameterNames =
  // Arrays.stream(objectClass.getDeclaredFields()).map(f -> f.getName()).toArray(String[]::new);
  // return new ConstructorPojoCreator<>(constructor, parameterNames,
  // columnToAccessorMap.getColumnAliasPrefix());
  // }

  private Constructor<T> getOrmConstructor(Class<T> objectClass) {
    List<Constructor<?>> ormConstructors = Arrays.stream(objectClass.getConstructors())
        .filter(c -> c.getAnnotation(OrmConstructor.class) != null).collect(Collectors.toList());
    if (ormConstructors.isEmpty()) {
      return null;
    } else if (ormConstructors.size() > 1) {
      throw new SormException(
          newString("Constructor with parameters annotated by {} should be one or less. ",
              OrmConstructor.class.getName()));
    } else {
      @SuppressWarnings("unchecked")
      Constructor<T> constructor = (Constructor<T>) ormConstructors.get(0);
      return constructor;
    }
  }

  private PojoCreator<T> createOrmConstructorPojoCreator(Class<T> objectClass,
      Constructor<T> constructor) {
    String[] _parameters = constructor.getAnnotation(OrmConstructor.class).value();
    return new ConstructorPojoCreator<>(getColumnToAccessorMap(), constructor, _parameters);
  }


  private Constructor<T> getDefaultConstructor(Class<T> objectClass) {
    return Try.getOrElseThrow(() -> objectClass.getConstructor(), e -> new SormException(newString(
        "The given container class [{}] should have the public default constructor (with no arguments) or the constructor annotated by [{}].",
        objectClass, OrmConstructor.class.getName()), e));
  }



  public String getFormattedString() {
    return "[" + ColumnsMapping.class.getSimpleName() + "] Columns are mappted to a class"
        + System.lineSeparator() + super.getColumnToAccessorString() + System.lineSeparator()
        + "  with [" + pojoCreator + "]";
  }

  public List<T> loadPojoList(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    return pojoCreator.loadPojoList(resultSetConverter, options, resultSet, metaData,
        createColumnLabels(resultSet, metaData));
  }

  public T loadPojo(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    return pojoCreator.loadPojo(resultSetConverter, options, resultSet, metaData,
        createColumnLabels(resultSet, metaData));
  }

  public String[] createColumnLabels(ResultSet resultSet, ResultSetMetaData metaData)
      throws SQLException {
    final int colNum = metaData.getColumnCount();
    final String[] columns = new String[colNum];
    for (int i = 1; i <= colNum; i++) {
      columns[i - 1] = metaData.getColumnLabel(i);
    }
    return columns;
  }



}
