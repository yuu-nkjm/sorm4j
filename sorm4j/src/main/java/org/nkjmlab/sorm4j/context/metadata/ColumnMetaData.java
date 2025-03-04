package org.nkjmlab.sorm4j.context.metadata;

public interface ColumnMetaData extends Comparable<ColumnMetaData> {

  String getTypeName();

  String getColumnName();
}
