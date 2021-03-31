package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.internal.util.StringUtils;

/**
 * Default implementation of {@link TableNameMapper}
 *
 * @author nkjm
 *
 */

public class DefaultTableNameMapper implements TableNameMapper {

  @Override
  public TableName getTableName(String tableName, DatabaseMetaData metaData) throws SQLException {
    List<String> candidates = List.of(tableName);
    return convertToExactTableName(metaData, candidates)
        .orElseThrow(() -> new SormException(StringUtils.format(
            "[{}] does not match any existing table in the database. Table Name candidates are {}",
            tableName, candidates)));
  }


  @Override
  public TableName getTableName(Class<?> objectClass, DatabaseMetaData metaData)
      throws SQLException {
    List<String> candidates = guessTableNameCandidates(objectClass);
    return convertToExactTableName(metaData, candidates)
        .orElseThrow(() -> new SormException(StringUtils.format(
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
    final OrmTable tableAnnotation = objectClass.getAnnotation(OrmTable.class);
    if (tableAnnotation != null && !tableAnnotation.value().equals("")) {
      return Arrays.asList(tableAnnotation.value());
    }

    String className = objectClass.getSimpleName();
    return StringUtils.addPluralSuffix(List.of(toCanonical(className)));
  }

  /**
   * Convert from the given table name candidates to the exact table name on the database.
   *
   * @param metaData
   * @param tableNameCandidates
   * @return
   * @throws SQLException
   */
  protected Optional<TableName> convertToExactTableName(DatabaseMetaData metaData,
      List<String> tableNameCandidates) throws SQLException {

    try (
        ResultSet resultSet = metaData.getTables(null, null, "%", new String[] {"TABLE", "VIEW"})) {
      while (resultSet.next()) {
        String tableNameOnDb = resultSet.getString(3);
        if (isMatch(tableNameCandidates, tableNameOnDb)) {
          return Optional.of(new TableName(tableNameOnDb));
        }
      }
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
