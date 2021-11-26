package org.nkjmlab.sorm4j.extension.impl;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.extension.TableName;
import org.nkjmlab.sorm4j.extension.TableNameMapper;

/**
 * Default implementation of {@link TableNameMapper}
 *
 * @author nkjm
 *
 */

public class DefaultTableNameMapper implements TableNameMapper {

  @Override
  public TableName getTableName(String tableName, DatabaseMetaData metaData) {
    List<String> candidates = List.of(tableName);
    return convertToExactTableName(metaData, candidates).orElseThrow(() -> new SormException(format(
        "[{}] does not match any existing table in the database. Use [{}] annotation correctly. Table Name candidates are {}",
        tableName, OrmTable.class.getName(), candidates)));
  }


  @Override
  public TableName getTableName(Class<?> objectClass, DatabaseMetaData metaData) {
    List<String> candidates = guessTableNameCandidates(objectClass);
    return convertToExactTableName(metaData, candidates).orElseThrow(() -> new SormException(format(
        "[{}] does not match any existing table in the database. Use [{}] annotation correctly. Table Name candidates are {}",
        objectClass.getName(), OrmTable.class.getName(), candidates)));
  }

  /**
   * Guesses table name from the given object class.
   *
   * @param objectClass
   * @return
   */
  protected List<String> guessTableNameCandidates(Class<?> objectClass) {

    List<String> annotatedTableName = Optional.ofNullable(objectClass.getAnnotation(OrmTable.class))
        .map(a -> List.of(a.value())).orElse(Collections.emptyList());

    if (annotatedTableName.size() != 0) {
      return annotatedTableName;
    }
    String className = objectClass.getSimpleName();
    String cannonicalClassName = toCanonicalCase(className);
    if (cannonicalClassName.endsWith("Y")) {
      return List.of(cannonicalClassName, cannonicalClassName + "S",
          cannonicalClassName.substring(0, cannonicalClassName.length() - 1) + "IES");
    } else {
      return List.of(cannonicalClassName, cannonicalClassName + "S");
    }
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
  protected Optional<TableName> convertToExactTableName(DatabaseMetaData metaData,
      List<String> tableNameCandidates) {
    try (
        ResultSet resultSet = metaData.getTables(null, null, "%", new String[] {"TABLE", "VIEW"})) {
      while (resultSet.next()) {
        String tableNameOnDb = resultSet.getString(3);
        if (isMatch(tableNameCandidates, tableNameOnDb)) {
          return Optional.of(new TableName(tableNameOnDb));
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }


  /**
   * Returns success or not: the given table name is match the one of the given candidates. That is
   * ignore case.
   *
   * @param candidates
   * @param exactTableName is the table name on the database
   * @return
   */
  protected boolean isMatch(List<String> candidates, String exactTableName) {
    return containsAsCanonical(candidates, exactTableName);
  }



}
