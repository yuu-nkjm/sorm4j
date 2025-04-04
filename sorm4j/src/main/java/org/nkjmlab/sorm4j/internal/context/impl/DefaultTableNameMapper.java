package org.nkjmlab.sorm4j.internal.context.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.TableNameMapper;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.mapping.annotation.OrmTableName;

/**
 * Default implementation of {@link TableNameMapper}
 *
 * @author nkjm
 */
public final class DefaultTableNameMapper implements TableNameMapper {

  private static final String ERROR_MESSAGE =
      "[{}] does not match any existing table in the database. Use @{} annotation or rename the class. Table name candidates were {}";

  @Override
  public String getTableName(String tableName, DatabaseMetaData metaData) {
    List<String> candidates = List.of(tableName);
    Object[] params = {tableName, OrmTableName.class.getSimpleName(), candidates};
    return convertToExactTableName(metaData, candidates)
        .orElseThrow(
            () ->
                new SormException(
                    ParameterizedStringFormatter.LENGTH_256.format(ERROR_MESSAGE, params)));
  }

  @Override
  public String getTableName(Class<?> objectClass, DatabaseMetaData metaData) {
    List<String> candidates = guessTableNameCandidates(objectClass);
    Object[] params = {objectClass.getName(), OrmTableName.class.getSimpleName(), candidates};
    return convertToExactTableName(metaData, candidates)
        .orElseThrow(
            () ->
                new SormException(
                    ParameterizedStringFormatter.LENGTH_256.format(ERROR_MESSAGE, params)));
  }

  /**
   * Guesses table name from the given object class.
   *
   * @param objectClass
   * @return
   */
  private List<String> guessTableNameCandidates(Class<?> objectClass) {

    Optional<String> annotatedTableName =
        Optional.ofNullable(objectClass.getAnnotation(OrmTableName.class)).map(a -> a.value());

    if (annotatedTableName.isPresent()) {
      return List.of(annotatedTableName.get());
    }

    String className = objectClass.getSimpleName();
    String cannonicalClassName = SormContext.getDefaultCanonicalStringCache().toCanonicalName(className);

    List<String> candidates =
        new ArrayList<>(
            List.of(
                cannonicalClassName,
                SormContext.getDefaultCanonicalStringCache().toCanonicalName(cannonicalClassName + "S"),
                SormContext.getDefaultCanonicalStringCache().toCanonicalName(cannonicalClassName + "ES")));
    if (cannonicalClassName.endsWith("Y")) {
      candidates.add(
          SormContext.getDefaultCanonicalStringCache()
              .toCanonicalName(
                  cannonicalClassName.substring(0, cannonicalClassName.length() - 1) + "IES"));
    }
    return candidates;
  }

  /**
   * Convert from the given table name candidates to the exact table name on the database.
   *
   * @param metaData
   * @param tableNameCandidates
   * @return
   * @throws SQLException
   * @see {@link java.sql.DatabaseMetaData#getTables}
   */
  private Optional<String> convertToExactTableName(
      DatabaseMetaData metaData, List<String> tableNameCandidates) {
    try (ResultSet resultSet =
        metaData.getTables(null, null, "%", new String[] {"TABLE", "VIEW"})) {
      while (resultSet.next()) {
        // 3. TABLE_NAME String => table name
        String tableNameOnDb = resultSet.getString(3);
        if (SormContext.getDefaultCanonicalStringCache().containsCanonicalName(tableNameCandidates, tableNameOnDb)) {
          return Optional.of(tableNameOnDb);
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
