package org.nkjmlab.sorm4j.util.datasource;

import javax.sql.DataSource;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.util.datasource.DriverManagerDataSourceImpl;

/**
 * A {@link DataSource} implementation that provides a simple way to create a database connection
 * using a JDBC URL, with optional username and password.
 *
 * <p>This interface offers factory methods to create a {@link DriverManagerDataSource} instance.
 *
 * <p>For example:
 *
 * <pre>
 * <code>
 *    DriverManagerDataSource dataSource = DriverManagerDataSource.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
 * </code>
 * </pre>
 */
public interface DriverManagerDataSource extends DataSource {

  /**
   * Creates a {@link DriverManagerDataSource} using the specified JDBC URL.
   *
   * <p>This method is a shorthand for calling {@link #create(String, String, String)} with {@code
   * null} for the username and password.
   *
   * <p>Example usage:
   *
   * <pre>
   * <code>
   *    DriverManagerDataSource dataSource = DriverManagerDataSource.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
   * </code>
   * </pre>
   *
   * @param jdbcUrl the JDBC URL for connecting to the database
   * @return a new instance of {@link DriverManagerDataSource}
   */
  public static DriverManagerDataSource create(String jdbcUrl) {
    return create(jdbcUrl, null, null);
  }

  /**
   * Creates a {@link DriverManagerDataSource} using the specified JDBC URL, username, and password.
   *
   * <p>This method provides a simple way to create a {@link Sorm} instance that connects to the
   * database using the given credentials.
   *
   * <p>If you need more precise control over database access, consider creating a {@link
   * DataSource} manually and using {@link #create(DataSource)} instead.
   *
   * <p>Example usage:
   *
   * <pre>
   * <code>
   *    DriverManagerDataSource dataSource = DriverManagerDataSource.create(
   *        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password");
   * </code>
   * </pre>
   *
   * @param jdbcUrl the JDBC URL for connecting to the database
   * @param username the username for authentication (can be {@code null})
   * @param password the password for authentication (can be {@code null})
   * @return a new instance of {@link DriverManagerDataSource}
   */
  public static DriverManagerDataSource create(String jdbcUrl, String username, String password) {
    return DriverManagerDataSourceImpl.create(jdbcUrl, username, password);
  }
}
