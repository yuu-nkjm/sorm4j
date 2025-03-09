package org.nkjmlab.sorm4j.internal.sql.metadata;

public interface ColumnMetaData extends Comparable<ColumnMetaData> {

  String getTypeName();

  String getColumnName();
}
