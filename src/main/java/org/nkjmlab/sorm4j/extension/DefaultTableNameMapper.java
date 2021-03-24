package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.core.util.StringUtils.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.core.util.StringUtils;

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
    return getTableNameOnDb(metaData, candidates)
        .orElseThrow(() -> new SormException(StringUtils.format(
            "[{}] does not match any existing table in the database. Table Name candidates are {}",
            tableName, candidates)));
  }


  @Override
  public TableName getTableName(Class<?> objectClass, DatabaseMetaData metaData)
      throws SQLException {

    final OrmTable tableAnnotation = objectClass.getAnnotation(OrmTable.class);
    List<String> candidates = (tableAnnotation != null && !tableAnnotation.value().equals(""))
        ? Arrays.asList(tableAnnotation.value())
        : guessTableNameCandidates(objectClass);
    return getTableNameOnDb(metaData, candidates)
        .orElseThrow(() -> new SormException(StringUtils.format(
            "[{}] does not match any existing table in the database. Use [{}] annotation correctly. Table Name candidates are {}",
            objectClass.getName(), OrmTable.class.getName(), candidates)));
  }

  protected List<String> guessTableNameCandidates(Class<?> objectClass) {
    String className = objectClass.getSimpleName();
    return StringUtils
        .addPluralSuffix(List.of(toUpperCase(className), toUpperSnakeCase(className)));
  }

  /**
   * Get the table name corresponding to the one of the given candidates from the database metadata.
   * That is ignore case matching.
   *
   * @param metaData
   * @param tableNameCandidates
   * @return
   * @throws SQLException
   */
  protected Optional<TableName> getTableNameOnDb(DatabaseMetaData metaData,
      List<String> tableNameCandidates) throws SQLException {

    try (
        ResultSet resultSet = metaData.getTables(null, null, "%", new String[] {"TABLE", "VIEW"})) {
      while (resultSet.next()) {
        String tableNameOnDb = resultSet.getString(3);
        if (StringUtils.containsIgnoreCase(tableNameCandidates, tableNameOnDb)) {
          return Optional.of(new TableName(tableNameOnDb));
        }
      }
      return Optional.empty();
    }
  }



}
