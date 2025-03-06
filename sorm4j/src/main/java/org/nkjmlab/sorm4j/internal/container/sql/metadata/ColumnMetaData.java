package org.nkjmlab.sorm4j.internal.container.sql.metadata;

public interface ColumnMetaData extends Comparable<ColumnMetaData> {

  String getTypeName();

  String getColumnName();
}
