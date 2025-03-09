package org.nkjmlab.sorm4j.internal.mapping.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nkjmlab.sorm4j.internal.OrmConnectionImpl.ColumnsAndTypes;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToJavaObjectConverters;

interface ResultsContainerFactory<T> {

  List<T> createContainerList(
      ColumnValueToJavaObjectConverters columnValueConverter,
      ResultSet resultSet,
      ColumnsAndTypes columnsAndTypes)
      throws SQLException;

  T createContainer(
      ColumnValueToJavaObjectConverters columnValueConverter,
      ResultSet resultSet,
      ColumnsAndTypes columnsAndTypes)
      throws SQLException;
}
