package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.internal.util.ResultSetStream;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

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

  protected List<String> guessTableNameCandidates(Class<?> objectClass) {
    final OrmTable tableAnnotation = objectClass.getAnnotation(OrmTable.class);
    if (tableAnnotation != null && !tableAnnotation.value().equals("")) {
      return Arrays.asList(tableAnnotation.value());
    }

    String className = objectClass.getSimpleName();
    return StringUtils.addPluralSuffix(List.of(toUpperCase(className)));
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
    return new ResultSetStream<>(
        metaData.getTables(null, null, "%", new String[] {"TABLE", "VIEW"}),
        Try.createFunctionWithThrow(rs -> {
          String exactTableNameOnDb = rs.getString(3);
          return exactTableNameOnDb;
        }, Try::rethrow)).stream()
            .filter(tableNameOnDb -> isMatch(tableNameCandidates, tableNameOnDb))
            .map(TableName::new).findFirst();
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
