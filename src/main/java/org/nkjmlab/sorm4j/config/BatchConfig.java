package org.nkjmlab.sorm4j.config;

public final class BatchConfig implements OrmConfig {

  private static final int DEFAULT_MULTI_ROW_SIZE = 32;
  private static final int DEFAULT_BATCH_SIZE = 32;
  private static final int DEFAULT_BATCH_WITH_MULTI_ROW_SIZE = 4;

  private final int multiRowSize;
  private final int batchSize;
  private final int batchSizeWithMultiRow;

  public BatchConfig() {
    this(DEFAULT_MULTI_ROW_SIZE, DEFAULT_BATCH_SIZE, DEFAULT_BATCH_WITH_MULTI_ROW_SIZE);
  }

  public BatchConfig(int multiRowSize, int batchSize, int batchSizeWithMultiRow) {
    this.multiRowSize = multiRowSize;
    this.batchSize = batchSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
  }

  public int getMultiRowSize() {
    return multiRowSize;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public int getBatchSizeWithMultiRow() {
    return batchSizeWithMultiRow;
  }

}
