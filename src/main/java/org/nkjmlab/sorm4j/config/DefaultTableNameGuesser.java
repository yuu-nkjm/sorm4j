package org.nkjmlab.sorm4j.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.util.StringUtils;

public final class DefaultTableNameGuesser implements TableNameMapper {

  @Override
  public String getTableName(final Class<?> objectClass, final Connection connection) {
    try {
      DatabaseMetaData metaData = connection.getMetaData();
      final OrmTable tableAnnotation = objectClass.getAnnotation(OrmTable.class);
      List<String> candidates = (tableAnnotation != null && !tableAnnotation.value().equals(""))
          ? Arrays.asList(tableAnnotation.value())
          : guessTableNameCandidates(objectClass);
      return getTableNameOnDb(metaData, candidates)
          .orElseThrow(() -> new OrmException(StringUtils.format(
              "[{}]  does not match a existing table in the db. Use [{}] annotation correctly. Candidates Name are {}",
              objectClass.getName(), OrmTable.class.getName(), candidates)));
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  List<String> guessTableNameCandidates(Class<?> objectClass) {
    return StringUtils
        .addPluralSuffix(List.of(StringUtils.toUpperSnakeCase(objectClass.getSimpleName())));
  }

  /**
   * Check if the given names corresponds to a table in the database and returns the corresponding
   * name returned by the database metadata
   */
  Optional<String> getTableNameOnDb(DatabaseMetaData metaData, List<String> tableNameCandidates) {

    try (
        ResultSet resultSet = metaData.getTables(null, null, "%", new String[] {"TABLE", "VIEW"})) {
      while (resultSet.next()) {
        String tableNameOnDb = resultSet.getString(3);
        if (StringUtils.containsIgnoreCase(tableNameCandidates, tableNameOnDb)) {
          return Optional.of(tableNameOnDb);
        }
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


}
