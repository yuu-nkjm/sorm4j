package org.nkjmlab.sorm4j.internal.common;

public interface ColumnMetaData extends Comparable<ColumnMetaData> {

  String getTypeName();

  String getColumnName();
}
