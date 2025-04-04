package repackage.net.sf.persist.tests.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.sql.result.InsertResult;

public class TestSimple {

  private static final JdbcConnectionPool dataSource =
      JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "sa", "");

  @BeforeAll
  static void beforAll() {}

  private static void dropAndCreateSimpleTable() {

    try (Connection conn = dataSource.getConnection()) {
      Statement st = conn.createStatement();
      st.executeUpdate("DROP TABLE SIMPLE IF EXISTS");
      st.executeUpdate(
          "CREATE TABLE SIMPLE (ID LONG AUTO_INCREMENT PRIMARY KEY, STRING_COL VARCHAR, LONG_COL BIGINT)");
    } catch (SQLException e) {
      fail();
    }
  }

  @BeforeEach
  void beforeEach() throws SQLException {
    dropAndCreateSimpleTable();
  }

  @AfterEach
  void afterEach() {}

  @AfterAll
  static void afterAll() throws SQLException {}

  public static Simple buildSimple() {
    Simple simple = new Simple();
    simple.setLongCol(randomLong(0, Long.MAX_VALUE / 2));
    simple.setStringCol(randomString(255));
    return simple;
  }

  @Test
  public void testNoTable() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      Simple simple = buildSimple();
      ormConn.insert(simple);

      SimpleNoTable simpleNoTable = ormConn.readFirst(SimpleNoTable.class, "select * from simple");
      assertEquals(simple.getLongCol(), simpleNoTable.getLongCol());
      assertEquals(simple.getStringCol(), simpleNoTable.getStringCol());
    }
  }

  @Test
  public void testExecuteUpdate() throws SQLException {

    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Simple createdSimple = buildSimple();

      // insert and check count of rows returned
      int n =
          ormConn.executeUpdate(
              "insert into simple (long_col, string_col) values(?,?)",
              createdSimple.getLongCol(),
              createdSimple.getStringCol());
      assertEquals(1, n);

      // read object and compare with inserted data
      Simple simpleRead =
          ormConn.readFirst(
              Simple.class,
              "select * from simple where long_col=? and string_col=?",
              createdSimple.getLongCol(),
              createdSimple.getStringCol());

      assertNotNull(simpleRead);
      assertEquals(createdSimple.getLongCol(), simpleRead.getLongCol());
      assertEquals(createdSimple.getStringCol(), simpleRead.getStringCol());

      // delete object and check it was removed
      ormConn.delete(simpleRead);
      simpleRead =
          ormConn.readFirst(
              Simple.class,
              "select * from simple where long_col=? and string_col=?",
              createdSimple.getLongCol(),
              createdSimple.getStringCol());
      assertNull(simpleRead);
    }
  }

  @Test
  public void testInsert() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection orm = OrmConnection.of(conn, SormContext.builder().build())) {
      orm.insert(List.of(buildSimple()));
      orm.insert(buildSimple(), buildSimple());
      orm.insertInto("simple", List.of(buildSimple()));
      orm.insertInto("simple", buildSimple(), buildSimple());
    }
  }

  @Test
  public void testExecuteUpdateAutoGeneratedKeys() throws SQLException {

    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      ormConn.insert(buildSimple());

      Simple simpleInsert = buildSimple();
      long longCol = simpleInsert.getLongCol();
      String stringCol = simpleInsert.getStringCol();

      InsertResult ret = ormConn.insertAndGet(simpleInsert);

      // insert with explicit auto generated keys and check result object data
      // InsertResult result = ormConn.executeUpdate(Simple.class,
      // "insert into simple (long_col,string_col) values(?,?)", intCol, stringCol);
      assertEquals(2, ret.getGeneratedKeys().getLong("id"));
      assertEquals(1, ret.getRowsModified()[0]);

      // read object and compare with inserted data
      Simple simpleRead =
          ormConn.readFirst(
              Simple.class,
              "select * from simple where long_col=? and string_col=?",
              longCol,
              stringCol);
      assertNotNull(simpleRead);
      assertEquals(longCol, simpleRead.getLongCol());
      assertEquals(stringCol, simpleRead.getStringCol());

      // delete object and check it was removed
      ormConn.delete(simpleRead);
      simpleRead =
          ormConn.readFirst(
              Simple.class,
              "select * from simple where long_col=? and string_col=?",
              longCol,
              stringCol);
      assertNull(simpleRead);
    }
  }

  @Test
  public void testSetAutoGeneratedKeys() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Simple s = TestSimple.buildSimple();
      InsertResult ret = ormConn.insertAndGet(s);
      long id =
          ormConn.readFirst(long.class, "select id from simple where long_col=?", s.getLongCol());

      assertEquals(id, ret.getGeneratedKeys().get("id"));

      // delete object by primary key and check it was removed
      ormConn.deleteByPrimaryKey(Simple.class, ret.getGeneratedKeys().get("id"));
      Simple simpleRead =
          ormConn.selectByPrimaryKey(Simple.class, ret.getGeneratedKeys().get("id"));
      assertNull(simpleRead);
    }
  }

  @Test
  public void testReturnNativeTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Simple simple = buildSimple();
      ormConn.insert(simple);
      long longCol =
          ormConn.readFirst(
              long.class, "select long_col from simple where long_col=?", simple.getLongCol());
      String stringCol =
          ormConn.readFirst(
              String.class, "select string_col from simple where long_col=?", simple.getLongCol());

      assertEquals(simple.getLongCol(), longCol);
      assertEquals(simple.getStringCol(), stringCol);
    }
  }

  @Test
  public void testBatch() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build()); ) {

      Simple simple1 = buildSimple();
      Simple simple2 = buildSimple();
      Simple simple3 = buildSimple();
      ormConn.insert(List.of(simple1, simple2, simple3));

      List<Simple> list = ormConn.selectAll(Simple.class);
      List<Simple> s = new ArrayList<>();
      s.add(simple1);
      s.add(simple2);
      s.add(simple3);
      assertTrue(s.containsAll(list));

      Simple s1 = list.get(0);
      Simple s2 = list.get(1);
      Simple s3 = list.get(2);

      s1.setLongCol(simple1.getLongCol() + 1);
      s1.setStringCol(simple1.getStringCol().toUpperCase());
      s2.setLongCol(simple2.getLongCol() + 1);
      s2.setStringCol(simple2.getStringCol().toUpperCase());
      s3.setLongCol(simple3.getLongCol() + 1);
      s3.setStringCol(simple3.getStringCol().toUpperCase());

      s = new ArrayList<>();
      s.add(s1);
      s.add(s2);
      s.add(s3);

      ormConn.update(List.of(s1, s2, s3));
      list = ormConn.selectAll(Simple.class);
      assertTrue(s.containsAll(list));

      ormConn.delete(List.of(s1, s2, s3));
      list = ormConn.selectAll(Simple.class);
      assertEquals(0, list.size());
    }
  }

  @Test
  public void testObject() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Simple simpleInsert = buildSimple();
      ormConn.insert(simpleInsert);

      long id = ormConn.readFirst(long.class, "select id from simple");

      Simple simpleUpdate = ormConn.selectByPrimaryKey(Simple.class, id);
      assertEquals(simpleInsert, simpleUpdate);

      simpleUpdate.setLongCol(randomLong(0, Integer.MAX_VALUE / 2));
      simpleUpdate.setStringCol(randomString(255));
      ormConn.update(simpleUpdate);

      Simple simpleRead = ormConn.selectByPrimaryKey(Simple.class, simpleUpdate.getId());
      assertEquals(simpleUpdate, simpleRead);

      ormConn.delete(simpleRead);

      Simple simpleDeleted = ormConn.selectByPrimaryKey(Simple.class, simpleRead.getId());
      assertNull(simpleDeleted);
    }
  }

  private static long randomLong(long i, long j) {
    return ThreadLocalRandom.current().nextLong(i, j);
  }

  private static String randomString(int i) {
    return String.valueOf(ThreadLocalRandom.current().nextInt(i));
  }

  @Test
  public void testObjectList() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Simple simple1 = buildSimple();
      Simple simple2 = buildSimple();
      Simple simple3 = buildSimple();
      ormConn.insert(simple1);
      ormConn.insert(simple2);
      ormConn.insert(simple3);

      List<Simple> list = ormConn.selectAll(Simple.class);
      List<Simple> s = new ArrayList<>();
      s.add(simple1);
      s.add(simple2);
      s.add(simple3);
      assertTrue(s.containsAll(list));

      ormConn.delete(list.get(0));
      ormConn.delete(list.get(1));
      ormConn.delete(list.get(2));
      List<Simple> listDeleted = ormConn.selectAll(Simple.class);
      assertTrue(0 == listDeleted.size());
    }
  }

  @Test
  public void testMap() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Simple simple = buildSimple();
      ormConn.insert(simple);

      long id = ormConn.readFirst(long.class, "select id from simple limit 1");

      Map<String, Object> simpleMap1 =
          ormConn.readFirst(
              RowMap.class, "select id, long_col, string_col from simple where id=?", id);
      assertEquals(id, simpleMap1.get("id"));
      assertEquals(simple.getLongCol(), simpleMap1.get("long_col"));
      assertEquals(simple.getStringCol(), simpleMap1.get("string_col"));

      ormConn.delete(simple);
    }
  }

  @Test
  public void testMapList() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.getDefaultContext())) {

      ormConn.executeUpdate("DELETE FROM SIMPLE");
      Simple simple1 = buildSimple();
      Simple simple2 = buildSimple();
      Simple simple3 = buildSimple();

      ormConn.insert(List.of(simple1, simple2, simple3));

      // tests using setAutoUpdateGeneratedKeys do not belong here
      List<Long> ids = ormConn.readList(long.class, "select id from simple order by id");
      simple1.setId(ids.get(0));
      simple2.setId(ids.get(1));
      simple3.setId(ids.get(2));

      List<RowMap> simpleList =
          ormConn.readList(
              RowMap.class,
              "select * from simple where id in (?,?,?)",
              simple1.getId(),
              simple2.getId(),
              simple3.getId());
      assertEquals(simple1.getId(), simpleList.get(0).get("id"));
      assertEquals(simple1.getLongCol(), simpleList.get(0).get("long_col"));
      assertEquals(simple1.getStringCol(), simpleList.get(0).get("string_col"));

      assertEquals(simple2.getId(), simpleList.get(1).get("id"));
      assertEquals(simple2.getLongCol(), simpleList.get(1).get("long_col"));
      assertEquals(simple2.getStringCol(), simpleList.get(1).get("string_col"));

      assertEquals(simple3.getId(), simpleList.get(2).get("id"));
      assertEquals(simple3.getLongCol(), simpleList.get(2).get("long_col"));
      assertEquals(simple3.getStringCol(), simpleList.get(2).get("string_col"));

      ormConn.delete(simple1);
      ormConn.delete(simple2);
      ormConn.delete(simple3);
    }
  }

  @Test
  public void testMappingSimple01() {
    // Simple01 specifies an invalid column name but field is used.
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      long id = ormConn.readFirst(long.class, "select id from simple limit 1");
      ormConn.selectByPrimaryKey(Simple01.class, id);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testMappingSimple02() {
    // Simple02 specifies an invalid table
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      long id = ormConn.readFirst(long.class, "select id from simple limit 1");
      ormConn.selectByPrimaryKey(Simple02.class, id);
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any existing table");
    }
  }

  @Test
  public void testMappingSimple03() {
    // Simple03 lacks a field
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build());
      ormConn.insert(buildSimple());
      ormConn.selectByPrimaryKey(Simple03.class, 1);
      // failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("[LONG_COL] does not match any field");
    }
  }

  @Test
  public void testMappingSimple04() {
    // Simple04 has incompatible setter
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      ormConn.selectByPrimaryKey(Simple04.class, 1);
      fail("Object with incompatible getter and setter did not trigger exception");
    } catch (Exception e) {
      org.assertj.core.api.Assertions.assertThat(e.getMessage()).contains("Could not set a value");
    }
  }

  @Test
  public void testMappingSimple05() {
    // Simple05 doesn't specify a table name and guessed names won't work
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      ormConn.selectByPrimaryKey(Simple05.class, 1);
      fail("Object with invalid table name did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any existing table");
    }
  }

  @Test
  public void testMappingSimple06() {
    // Simple06 has different annotations for getter and setter
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      ormConn.selectByPrimaryKey(Simple06.class, 1);
      // fail("Object without getter and setter did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any field");
    }
  }

  @Test
  public void testMappingSimple07() {
    // Simple07 doesn't have a getter and setter for string_col
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      ormConn.selectByPrimaryKey(Simple07.class, 1);
      // fail("Object without getter and setter did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("[STRING_COL] does not match any field");
    }
  }

  @Test
  public void testMappingSimple08() {
    // Simple07 doesn't have a getter and setter for string_col
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      ormConn.selectByPrimaryKey(Simple08.class, 1);
      // fail("Object with conflicting annotations did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any field");
    }
  }

  @Test
  public void testMappingSimple10() {
    // Simple10 has setter with no parameters
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.selectByPrimaryKey(Simple10.class, 1);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testMappingSimple11() {
    // Simple10 has setter with no parameters
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(new Simple11());
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have");
    }
  }

  @Test
  public void testMappingSimple12() {
    // Simple10 has setter with no parameters
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(new Simple12(1));
      ormConn.selectAll(Simple12.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("default constructor");
    }
  }

  @Test
  public void testMapping() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {
      ormConn.insert(buildSimple());
      long id = ormConn.readFirst(long.class, "select id from simple limit 1");
      // Simple09 has getter which returns void
      try {
        ormConn.selectByPrimaryKey(Simple09.class, id);
        // fail("Object with getter returning void did not trigger exception");
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("does not match any field");
      }
    }
  }
}
