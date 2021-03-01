package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.util.DebugPoint;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.PreparedStatementUtils;
import org.nkjmlab.sorm4j.util.Try;

public abstract class MultiRowProcessor<T> {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  final TableMapping<T> tableMapping;
  private final int batchSize;

  MultiRowProcessor(TableMapping<T> tableMapping, int batchSize) {
    this.tableMapping = tableMapping;
    this.batchSize = batchSize;
  }

  public abstract int[] multiRowInsert(Connection con, @SuppressWarnings("unchecked") T... objects);

  public abstract int[] multiRowMerge(Connection con, @SuppressWarnings("unchecked") T... objects);

  void setAutoCommit(Connection connection, boolean autoCommit) {
    Try.runOrThrow(() -> connection.setAutoCommit(autoCommit), OrmException::new);
  }

  void commitIfRequired(Connection connection, boolean origAutoCommit) {
    if (origAutoCommit) {
      Try.runOrThrow(() -> connection.commit(), OrmException::new);
    }
  }


  void rollbackIfRequired(Connection connection, boolean origAutoCommit) {
    if (!origAutoCommit) {
      Try.runOrThrow(() -> connection.rollback(), OrmException::new);
    }
  }

  boolean getAutoCommit(Connection connection) {
    return Try.createSupplierWithThrow(() -> connection.getAutoCommit(), OrmException::new).get();
  }

  public int[] batch(Connection con, String sql, Function<T, Object[]> parameterCreator,
      T[] objects) {
    return execIfValidObjects(con, objects, nonNullObjects -> {
      return batchAux(con, sql, obj -> parameterCreator.apply(obj), nonNullObjects);
    });
  }

  private int[] batchAux(Connection con, String sql, Function<T, Object[]> parameterCreator,
      T[] objects) {

    int[] result = new int[0];
    boolean origAutoCommit = getAutoCommit(con);

    try (PreparedStatement stmt = PreparedStatementUtils.getPreparedStatement(con, sql)) {
      setAutoCommit(con, false);
      final BatchHelper batchHelper = new BatchHelper(batchSize, stmt);
      for (int i = 0; i < objects.length; i++) {
        T obj = objects[i];
        this.tableMapping.javaToSqlConverter.setParameters(stmt, parameterCreator.apply(obj));
        batchHelper.addBatchAndExecuteIfReachedThreshold();
      }
      result = batchHelper.finish();
      return result;
    } catch (Exception e) {
      rollbackIfRequired(con, origAutoCommit);
      throw new OrmException(e);
    } finally {
      commitIfRequired(con, origAutoCommit);
      setAutoCommit(con, origAutoCommit);
    }
  }


  @SuppressWarnings("unchecked")
  int[] execIfValidObjects(Connection con, T[] objects, Function<T[], int[]> exec) {
    if (objects == null || objects.length == 0) {
      return new int[0];
    }
    Optional<DebugPoint> dp =
        DebugPointFactory.createDebugPoint(DebugPointFactory.Name.EXECUTE_BATCH);

    T[] nonNullObjects =
        (T[]) Arrays.stream(objects).filter(Objects::nonNull).toArray(Object[]::new);
    int[] result = exec.apply(nonNullObjects);

    dp.ifPresent(sw -> log.debug("{} [{}] objects (req=[{}]) of [{}] are wrote into [{}]  at [{}]",
        sw.getFormattedNameAndElapsedTime(), IntStream.of(result).sum(), objects.length,
        tableMapping.objectClass, tableMapping.getTableName(),
        Try.getOrNull(() -> con.getMetaData().getURL())));
    return result;
  }


}
