package repackage.net.sf.persist.tests.engine.framework;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.sorm4j.util.datasource.DriverManagerDataSource;

public class DbEngineTestUtils {

  public static void executeSql(DataSource dataSource, Class<?> clazz, String fileName) {
    try (Connection conn = dataSource.getConnection()) {
      Statement st = conn.createStatement();
      String[] sqls =
          String.join(
                  System.lineSeparator(),
                  Files.readAllLines(new File(clazz.getResource(fileName).toURI()).toPath()))
              .split(";");
      Arrays.asList(sqls)
          .forEach(
              sql -> {
                try {
                  sql = sql.trim();
                  if (sql.length() < 4) {
                    return;
                  }
                  st.execute(sql);
                } catch (SQLException e) {
                  System.err.println(sql);
                  System.err.println(e.getMessage());
                }
              });
    } catch (SQLException | URISyntaxException | IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public static DataSource getDataSource(Class<?> clazz, String defaultJdbcUrl) {
    try {
      Properties properties = new Properties();
      properties.load(clazz.getResourceAsStream("db.properties"));
      String url = properties.getProperty("url");
      String user = properties.getProperty("username");
      String password = properties.getProperty("password");
      return DriverManagerDataSource.create(url, user, password);
    } catch (Exception e) {
      return JdbcConnectionPool.create(defaultJdbcUrl, "sorm", "sorm");
    }
  }
}
