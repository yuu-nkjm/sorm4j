package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessor;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactoryImpl;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

public interface MultiRowProcessorFactory {


  /**
   * Type of how to execute multi-row update SQL statements.
   */
  public enum MultiRowProcessorType {
    SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH;
  }


  public static Builder builder() {
    return new Builder();
  }


  public static class Builder {

    private MultiRowProcessorType multiRowProcessorType = MultiRowProcessorType.MULTI_ROW;
    private int batchSize = 32;
    private int multiRowSize = 32;
    private int batchSizeWithMultiRow = 5;


    public Builder setBatchSize(int size) {
      this.batchSize = size;
      return this;
    }


    public Builder setMultiRowSize(int size) {
      this.multiRowSize = size;
      return this;
    }


    public Builder setBatchSizeWithMultiRow(int size) {
      this.batchSizeWithMultiRow = size;
      return this;
    }


    public Builder setMultiRowProcessorType(MultiRowProcessorType type) {
      this.multiRowProcessorType = type;
      return this;
    }

    public MultiRowProcessorFactory build() {
      return new MultiRowProcessorFactoryImpl(multiRowProcessorType, batchSize, multiRowSize,
          batchSizeWithMultiRow);
    }

  }


  <T> MultiRowProcessor<T> getMultiRowProcessor(LoggerContext loggerContext,
      SqlParametersSetter sqlParametersSetter, PreparedStatementSupplier statementSupplier,
      Class<T> objectClass, SqlParametersToTableMapping<T> sqlParametersToTableMapping);


}
