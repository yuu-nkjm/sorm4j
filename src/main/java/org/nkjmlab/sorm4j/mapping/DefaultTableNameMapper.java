package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.StringUtils.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.config.TableNameMapper;
import org.nkjmlab.sorm4j.util.StringUtils;

public final class DefaultTableNameMapper implements TableNameMapper {


  @Override
  public TableName toValidTableName(String tableName, DatabaseMetaData metaData)
      throws SQLException {
    List<String> candidates = List.of(StringUtils.toUpperCase(tableName));
    return getTableNameOnDb(metaData, candidates)
        .orElseThrow(() -> new OrmException(StringUtils.format(
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
        .orElseThrow(() -> new OrmException(StringUtils.format(
            "[{}] does not match any existing table in the database. Use [{}] annotation correctly. Table Name candidates are {}",
            objectClass.getName(), OrmTable.class.getName(), candidates)));
  }

  List<String> guessTableNameCandidates(Class<?> objectClass) {
    String className = objectClass.getSimpleName();
    return StringUtils.addPluralSuffix(
        List.of(toUpperCase(className), toLowerCase(className), toUpperSnakeCase(className)));
  }

  /**
   * Check if the given names corresponds to a table in the database and returns the corresponding
   * name returned by the database metadata
   *
   * @param metaData
   * @param tableNameCandidates
   * @return
   * @throws SQLException
   */
  public Optional<TableName> getTableNameOnDb(DatabaseMetaData metaData,
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
