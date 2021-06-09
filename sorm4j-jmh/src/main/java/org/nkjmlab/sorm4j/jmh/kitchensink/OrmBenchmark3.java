package org.nkjmlab.sorm4j.jmh.kitchensink;

import static org.nkjmlab.sorm4j.jmh.kitchensink.OrmBenchmark3.Post.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.nkjmlab.sorm4j.Sorm;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.tools.FeatureDetector;

/**
 *
 * This code is based on following code at first.
 * https://github.com/aaberg/sql2o/blob/master/core/src/test/java/org/sql2o/performance/PojoPerformanceTest.java
 *
 * @author nkjm
 *
 */
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Threads(1)
@Fork(1)
@State(Scope.Thread)
public class OrmBenchmark3 {
  private final static String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private final static String DB_USER = "sa";
  private final static String DB_PASSWORD = "";
  private static final javax.sql.DataSource dataSource =
      JdbcConnectionPool.create(DB_URL, DB_USER, DB_PASSWORD);

  private static final HandCodedSelect handCodedSelect = new HandCodedSelect();
  private static final Sql2oSelect sql2oSelect = new Sql2oSelect();
  private static final JdbiSelect jdbiSelect = new JdbiSelect();
  private static final JooqSelect jooqSelect = new JooqSelect();
  private static final ApacheDbUtilsSelect apacheDbUtilsSelect = new ApacheDbUtilsSelect();
  private static final MyBatisSelect myBatisSelect = new MyBatisSelect();
  private static final SpringJdbcTemplateSelect springJdbcTemplateSelect =
      new SpringJdbcTemplateSelect();
  private static final SormSelect sormSelect = new SormSelect();

  private static final int NUM_OF_ROWS = 10240;

  public static void main(String[] args) {

    OrmBenchmark3 b = new OrmBenchmark3();
    b.setup();
    b.handCodedSelect();
    b.sql2oTypicalSelect();
    b.sql2oOptimalSelect();
    b.jDBISelect();
    b.jOOQSelect();
    b.apacheDbUtilsSelect();
    b.myBatisSelect();
    b.springJdbcTemplateSelect();
    b.sormSelectByPrimaryKey();

  }

  @Setup
  public void setup() {
    System.out.println(System.lineSeparator() + "### setup ##################");
    Sorm sorm = Sorm.create(dataSource);
    sorm.accept(conn -> {
      conn.executeUpdate(Post.DROP_AND_CREATE_TABLE);
      conn.insert(
          IntStream.range(0, NUM_OF_ROWS).mapToObj(i -> Post.createRandom(i)).toArray(Post[]::new));
    });
  }

  @Benchmark
  public void handCodedSelect() {
    handCodedSelect.run(idGenerator());
  }

  @Benchmark
  public void sql2oTypicalSelect() {
    sql2oSelect.run(idGenerator());
  }

  @Benchmark
  public void sql2oOptimalSelect() {
    sql2oSelect.selectSingleRowWithOptimally(idGenerator());
  }

  @Benchmark
  public void jDBISelect() {
    jdbiSelect.run(idGenerator());
  }

  @Benchmark
  public void jOOQSelect() {
    jooqSelect.run(idGenerator());
  }

  @Benchmark
  public void apacheDbUtilsSelect() {
    apacheDbUtilsSelect.run(idGenerator());
  }

  @Benchmark
  public void myBatisSelect() {
    myBatisSelect.run(idGenerator());
  }

  @Benchmark
  public void springJdbcTemplateSelect() {
    springJdbcTemplateSelect.run(idGenerator());
  }

  @Benchmark
  public void sormSelectByPrimaryKey() {
    sormSelect.run(idGenerator());
  }

  @Benchmark
  public void sormSelectByReadFirst() {
    sormSelect.runReadFirst(idGenerator());
  }

  /**
   * Note: id is auto-incremented between 1 to NUM_OF_ROWS;
   *
   * @return
   */
  private static final int idGenerator() {
    return ThreadLocalRandom.current().nextInt(1, NUM_OF_ROWS);
  }

  static class Sql2oSelect implements BenchmarkBase {
    private static Sql2o sql2o = new Sql2o(dataSource);
    static {
      // turn off oracle because ResultSetUtils slows down with oracle
      setOracleAvailable(false);
    }

    @Override
    public void run(int input) {
      try (org.sql2o.Connection conn = sql2o.open()) {
        Query query =
            conn.createQuery(SELECT_TYPICAL_SQL + " WHERE id = :id").setAutoDeriveColumnNames(true);
        query.addParameter("id", input).executeAndFetchFirst(Post.class);
      }
    }

    public void selectSingleRowWithOptimally(int input) {
      try (org.sql2o.Connection conn = sql2o.open()) {
        Query query = conn.createQuery(SELECT_OPTIMAL_SQL + " WHERE id = :id");
        query.addParameter("id", input).executeAndFetchFirst(Post.class);
      }
    }

    private static void setOracleAvailable(boolean b) {
      try {
        Field f = FeatureDetector.class.getDeclaredField("oracleAvailable");
        f.setAccessible(true);
        f.set(null, b);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * It appears JDBI does not support mapping underscore to camel case.
   */
  static class JdbiSelect implements BenchmarkBase {

    private static final Jdbi jdbi = Jdbi.create(dataSource);


    @Override
    public void run(int input) {
      jdbi.withHandle(handler -> handler.createQuery(SELECT_TYPICAL_SQL + " WHERE id=?")
          .bind(0, input).mapToBean(Post.class).findFirst()).get();

    }

  }

  static class JooqSelect implements BenchmarkBase {
    @Override
    public void run(int input) {
      try (Connection conn = dataSource.getConnection()) {
        DSLContext context = DSL.using(conn, SQLDialect.H2);
        context.select().from("post").where("id = ?", input).fetchOne().into(Post.class);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }


  static class HandCodedSelect implements BenchmarkBase {


    private static final Integer getNullableInt(ResultSet rs, String colName) throws SQLException {
      Object obj = rs.getObject(colName);
      return obj == null ? null : (Integer) obj;
    }


    @Override
    public void run(int input) {
      try (Connection conn = dataSource.getConnection();
          PreparedStatement stmt = conn.prepareStatement(SELECT_TYPICAL_SQL + " WHERE id = ?")) {
        stmt.setInt(1, input);
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            Post p = new Post();
            p.setId(rs.getInt("id"));
            p.setText(rs.getString("text"));
            p.setCreationDate(rs.getDate("creation_date"));
            p.setLastChangeDate(rs.getDate("last_change_date"));
            p.setCounter1(getNullableInt(rs, "counter1"));
            p.setCounter2(getNullableInt(rs, "counter2"));
            p.setCounter3(getNullableInt(rs, "counter3"));
            p.setCounter4(getNullableInt(rs, "counter4"));
            p.setCounter5(getNullableInt(rs, "counter5"));
            p.setCounter6(getNullableInt(rs, "counter6"));
            p.setCounter7(getNullableInt(rs, "counter7"));
            p.setCounter8(getNullableInt(rs, "counter8"));
            p.setCounter9(getNullableInt(rs, "counter9"));
          }
        }
      } catch (SQLException e) {
        throw new RuntimeException("error when executing query", e);
      }
    }

  }


  static class ApacheDbUtilsSelect implements BenchmarkBase {
    private static final QueryRunner runner = new QueryRunner();
    private static final ResultSetHandler<Post> rsHandler = new BeanHandler<Post>(Post.class,
        new BasicRowProcessor(new IgnoreUnderscoreBeanProcessor()));;

    /**
     * This class handles mapping "first_name" column to "firstName" property. It looks worse than
     * it is, most is copied from {@link org.apache.commons.dbutils.BeanProcessor} and many people
     * complain online that this isn't built in to Apache DbUtils yet.
     */
    private static class IgnoreUnderscoreBeanProcessor extends BeanProcessor {

      @Override
      protected int[] mapColumnsToProperties(ResultSetMetaData md, PropertyDescriptor[] props)
          throws SQLException {
        int cols = md.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
          String columnName = md.getColumnLabel(col);
          if (null == columnName || 0 == columnName.length()) {
            columnName = md.getColumnName(col);
          }
          String noUnderscoreColName = columnName.replace("_", ""); // this is the addition from
                                                                    // BeanProcessor
          for (int i = 0; i < props.length; i++) {
            if (noUnderscoreColName.equalsIgnoreCase(props[i].getName())) {
              columnToProperty[col] = i;
              break;
            }
          }
        }

        return columnToProperty;
      }
    }


    @Override
    public void run(int input) {
      try (Connection conn = dataSource.getConnection()) {
        runner.query(conn, SELECT_TYPICAL_SQL + " WHERE id = ?", rsHandler, input);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

  }


  /**
   * It appears executing raw SQL is not possible with MyBatis. Therefore "typical" = "optimized",
   * there is no difference.
   */
  static class MyBatisSelect implements BenchmarkBase {
    private static final SqlSessionFactory sqlSessionFactory;
    static {
      TransactionFactory transactionFactory = new JdbcTransactionFactory();
      Environment environment = new Environment("development", transactionFactory, dataSource);
      org.apache.ibatis.session.Configuration config =
          new org.apache.ibatis.session.Configuration(environment);
      config.addMapper(MyBatisPostMapper.class);
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
    }


    @Override
    public void run(int input) {
      try (SqlSession session = sqlSessionFactory.openSession()) {
        session.getMapper(MyBatisPostMapper.class).selectPost(input);
      }
    }

    private interface MyBatisPostMapper {
      @Select(SELECT_TYPICAL_SQL + " WHERE id = #{id}")
      @Results({@Result(property = "creationDate", column = "creation_date"),
          @Result(property = "lastChangeDate", column = "last_change_date")})
      Post selectPost(int id);
    }

  }



  static class SpringJdbcTemplateSelect implements BenchmarkBase {
    private static final NamedParameterJdbcTemplate jdbcTemplate =
        new NamedParameterJdbcTemplate(dataSource);

    @Override
    public void run(int input) {
      jdbcTemplate.queryForObject(SELECT_TYPICAL_SQL + " WHERE id = :id",
          Collections.singletonMap("id", input), new BeanPropertyRowMapper<Post>(Post.class));
    }

  }

  static class SormSelect implements BenchmarkBase {
    private static final Sorm sorm = Sorm.create(dataSource);

    @Override
    public void run(int input) {
      sorm.apply(conn -> conn.readByPrimaryKey(Post.class, input));
    }

    public void runReadFirst(int input) {
      sorm.apply(
          conn -> conn.readFirst(Post.class, Post.SELECT_TYPICAL_SQL + " WHERE id=?", input));
    }

  }

  public static interface BenchmarkBase {
    void run(int input);

  }

  public static class Post {
    public static final String DROP_AND_CREATE_TABLE = "DROP TABLE IF EXISTS post;"
        + "CREATE TABLE post ( "
        + "id INT NOT NULL IDENTITY PRIMARY KEY , text VARCHAR(255) , creation_date DATETIME , last_change_date DATETIME , "
        + "counter1 INT , counter2 INT , counter3 INT , counter4 INT , counter5 INT , counter6 INT , counter7 INT , counter8 INT , counter9 INT );";
    public static final String SELECT_TYPICAL_SQL = "SELECT * FROM post";
    public static final String SELECT_OPTIMAL_SQL =
        "SELECT id, text, creation_date as creationDate, last_change_date as lastChangeDate, counter1, counter2, counter3, counter4, counter5, counter6, counter7, counter8, counter9 FROM post";

    public int id;
    private String text;
    private Date creationDate;
    private Date lastChangeDate;
    private Integer counter1;
    private Integer counter2;
    private Integer counter3;
    private Integer counter4;
    private Integer counter5;
    private Integer counter6;
    private Integer counter7;
    private Integer counter8;
    private Integer counter9;

    public int getId() {
      return id;
    }

    public static Post createRandom(int idx) {
      ThreadLocalRandom r = ThreadLocalRandom.current();
      Post p = new Post();
      p.text = "a name " + idx;
      p.creationDate = new DateTime(System.currentTimeMillis() + r.nextInt()).toDate();
      p.lastChangeDate = new DateTime(System.currentTimeMillis() + r.nextInt()).toDate();
      p.counter1 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter2 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter3 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter4 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter5 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter6 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter7 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter8 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter9 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      return p;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public Date getCreationDate() {
      return creationDate;
    }

    public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
    }

    public Date getLastChangeDate() {
      return lastChangeDate;
    }

    public void setLastChangeDate(Date lastChangeDate) {
      this.lastChangeDate = lastChangeDate;
    }

    public Integer getCounter1() {
      return counter1;
    }

    public void setCounter1(Integer counter1) {
      this.counter1 = counter1;
    }

    public Integer getCounter2() {
      return counter2;
    }

    public void setCounter2(Integer counter2) {
      this.counter2 = counter2;
    }

    public Integer getCounter3() {
      return counter3;
    }

    public void setCounter3(Integer counter3) {
      this.counter3 = counter3;
    }

    public Integer getCounter4() {
      return counter4;
    }

    public void setCounter4(Integer counter4) {
      this.counter4 = counter4;
    }

    public Integer getCounter5() {
      return counter5;
    }

    public void setCounter5(Integer counter5) {
      this.counter5 = counter5;
    }

    public Integer getCounter6() {
      return counter6;
    }

    public void setCounter6(Integer counter6) {
      this.counter6 = counter6;
    }

    public Integer getCounter7() {
      return counter7;
    }

    public void setCounter7(Integer counter7) {
      this.counter7 = counter7;
    }

    public Integer getCounter8() {
      return counter8;
    }

    public void setCounter8(Integer counter8) {
      this.counter8 = counter8;
    }

    public Integer getCounter9() {
      return counter9;
    }

    public void setCounter9(Integer counter9) {
      this.counter9 = counter9;
    }
  }
}
