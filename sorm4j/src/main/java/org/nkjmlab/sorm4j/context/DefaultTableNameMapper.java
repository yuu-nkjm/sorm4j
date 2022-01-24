package org.nkjmlab.sorm4j.context;

import static org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.common.SormException;

/**
 * Default implementation of {@link TableNameMapper}
 *
 * @author nkjm
 *
 */

public final class DefaultTableNameMapper implements TableNameMapper {

  @Override
  public String getTableName(String tableName, DatabaseMetaData metaData) {
    List<String> candidates = List.of(tableName);
    return convertToExactTableName(metaData, candidates)
        .orElseThrow(() -> new SormException(newString(
            "[{}] does not match any existing table in the database. Use [{}] annotation correctly. Table Name candidates are {}",
            tableName, OrmTable.class.getName(), candidates)));
  }


  @Override
  public String getTableName(Class<?> objectClass, DatabaseMetaData metaData) {
    List<String> candidates = guessTableNameCandidates(objectClass);
    return convertToExactTableName(metaData, candidates)
        .orElseThrow(() -> new SormException(newString(
            "[{}] does not match any existing table in the database. Use [{}] annotation correctly. Table Name candidates are {}",
            objectClass.getName(), OrmTable.class.getName(), candidates)));
  }

  /**
   * Guesses table name from the given object class.
   *
   * @param objectClass
   * @return
   */
  private List<String> guessTableNameCandidates(Class<?> objectClass) {

    List<String> annotatedTableName = Optional.ofNullable(objectClass.getAnnotation(OrmTable.class))
        .map(a -> List.of(a.value())).orElse(Collections.emptyList());

    if (!annotatedTableName.isEmpty()) {
      return annotatedTableName;
    }
    String className = objectClass.getSimpleName();
    String cannonicalClassName = toCanonicalCase(className);

    return cannonicalClassName.endsWith("Y")
        ? List.of(cannonicalClassName, cannonicalClassName + "S",
            cannonicalClassName.substring(0, cannonicalClassName.length() - 1) + "IES")
        : List.of(cannonicalClassName, cannonicalClassName + "S");

  }


  /**
   * Convert from the given table name candidates to the exact table name on the database.
   *
   * @param metaData
   * @param tableNameCandidates
   * @return
   * @throws SQLException
   *
   * @see {@link java.sql.DatabaseMetaData#getTables}
   */
  private Optional<String> convertToExactTableName(DatabaseMetaData metaData,
      List<String> tableNameCandidates) {
    try (
        ResultSet resultSet = metaData.getTables(null, null, "%", new String[] {"TABLE", "VIEW"})) {
      while (resultSet.next()) {
        // 3. TABLE_NAME String => table name
        String tableNameOnDb = resultSet.getString(3);
        if (containsAsCanonical(tableNameCandidates, tableNameOnDb)) {
          return Optional.of(tableNameOnDb);
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }



}
