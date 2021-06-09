package org.nkjmlab.sorm4j.extension;

/**
 * Type of how to execute multi-row update SQL statements.
 */
public enum MultiRowProcessorType {
  SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH
}