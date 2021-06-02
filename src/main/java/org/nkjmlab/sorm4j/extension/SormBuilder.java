package org.nkjmlab.sorm4j.extension;

import java.util.function.Supplier;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.extension.logger.SormLogger;
import org.nkjmlab.sorm4j.internal.mapping.SormImpl;

@Experimental
public class SormBuilder {


  private DataSource dataSource;
  private SormConfigBuilder configBuilder = new SormConfigBuilder();

  public SormBuilder() {}

  public SormBuilder(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Sorm build() {
    return new SormImpl(dataSource, configBuilder.build());
  }

  public SormBuilder setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    return this;
  }


  public SormBuilder setDataSource(String jdbcUrl, String username, String password) {
    this.dataSource = Sorm.createDriverManagerDataSource(jdbcUrl, username, password);
    return this;
  }

  public SormBuilder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper) {
    configBuilder.setColumnFieldMapper(fieldNameMapper);
    return this;
  }


  public SormBuilder setTableNameMapper(TableNameMapper tableNameMapper) {
    configBuilder.setTableNameMapper(tableNameMapper);
    return this;
  }


  public SormBuilder setResultSetConverter(ResultSetConverter resultSetConverter) {
    configBuilder.setResultSetConverter(resultSetConverter);
    return this;
  }


  public SormBuilder setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
    configBuilder.setSqlParametersSetter(sqlParametersSetter);
    return this;
  }


  public SormBuilder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType) {
    configBuilder.setMultiRowProcessorType(multiRowProcessorType);
    return this;
  }


  public SormBuilder setBatchSize(int size) {
    configBuilder.setBatchSize(size);
    return this;
  }


  public SormBuilder setMultiRowSize(int size) {
    configBuilder.setMultiRowSize(size);
    return this;
  }


  public SormBuilder setBatchSizeWithMultiRow(int size) {
    configBuilder.setBatchSizeWithMultiRow(size);
    return this;
  }


  public SormBuilder setTransactionIsolationLevel(int level) {
    configBuilder.setTransactionIsolationLevel(level);
    return this;
  }


  public SormBuilder setOption(String name, Object value) {
    configBuilder.setOption(name, value);
    return this;
  }


  public SormBuilder setLoggerOnAll() {
    configBuilder.setLoggerOnAll();
    return this;
  }

  public SormBuilder setLoggerOffAll() {
    configBuilder.setLoggerOffAll();
    return this;
  }

  public SormBuilder setLoggerOn(LoggerConfig.Category... categories) {
    configBuilder.setLoggerOn(categories);
    return this;
  }

  public SormBuilder setLoggerOff(LoggerConfig.Category... categories) {
    configBuilder.setLoggerOff(categories);
    return this;
  }

  public SormBuilder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
    configBuilder.setLoggerSupplier(loggerSupplier);
    return this;
  }


}
