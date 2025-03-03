package org.nkjmlab.sorm4j.common;

public interface ColumnMetaData extends Comparable<ColumnMetaData> {

  String getTypeName();

  String getColumnName();
}
