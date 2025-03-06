package org.nkjmlab.sorm4j.internal.container;

public interface ColumnMetaData extends Comparable<ColumnMetaData> {

  String getTypeName();

  String getColumnName();
}
