package repackage.net.sf.persist.tests.common;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmMapper;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.StringUtils;

public class TestSimple {

  private static org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  private static final JdbcConnectionPool connectionPool =
      JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "sa", "");

  @BeforeAll
  static void beforAll() {
    DebugPointFactory.setModes(Map.of(DebugPointFactory.Name.MAPPING, true,
        DebugPointFactory.Name.READ, true, DebugPointFactory.Name.LOAD_OBJECT, true,
        DebugPointFactory.Name.EXECUTE_BATCH, true, DebugPointFactory.Name.EXECUTE_UPDATE, true));
  }

  private static void dropAndCreateSimpleTable() {

    try (Connection conn = connectionPool.getConnection()) {
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
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      Simple simple = buildSimple();
      simpleOrMapper.insert(simple);

      SimpleNoTable simpleNoTable =
          simpleOrMapper.readFirst(SimpleNoTable.class, "select * from simple");
      assertEquals(simple.getLongCol(), simpleNoTable.getLongCol());
      assertEquals(simple.getStringCol(), simpleNoTable.getStringCol());
    }
  }


  @Test
  public void testExecuteUpdate() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);

      Simple createdSimple = buildSimple();

      // insert and check count of rows returned
      int n = simpleOrMapper.executeUpdate("insert into simple (long_col, string_col) values(?,?)",
          createdSimple.getLongCol(), createdSimple.getStringCol());
      assertEquals(1, n);

      // read object and compare with inserted data
      Simple simpleRead = simpleOrMapper.readFirst(Simple.class,
          "select * from simple where long_col=? and string_col=?", createdSimple.getLongCol(),
          createdSimple.getStringCol());

      assertNotNull(simpleRead);
      assertEquals(createdSimple.getLongCol(), simpleRead.getLongCol());
      assertEquals(createdSimple.getStringCol(), simpleRead.getStringCol());

      // delete object and check it was removed
      simpleOrMapper.delete(simpleRead);
      simpleRead = simpleOrMapper.readFirst(Simple.class,
          "select * from simple where long_col=? and string_col=?", createdSimple.getLongCol(),
          createdSimple.getStringCol());
      assertNull(simpleRead);
    }
  }

  @Test
  public void testExecuteUpdateAutoGeneratedKeys() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      TypedOrmConnection<Simple> simpleOrMapper = Sorm.getTypedOrmConnection(conn, Simple.class);

      simpleOrMapper.insert(buildSimple());

      Simple simpleInsert = buildSimple();
      long longCol = simpleInsert.getLongCol();
      String stringCol = simpleInsert.getStringCol();

      InsertResult<Simple> result = simpleOrMapper.insertAndGet(simpleInsert);

      // insert with explicit auto generated keys and check result object data
      // InsertResult result = simpleOrMapper.executeUpdate(Simple.class,
      // "insert into simple (long_col,string_col) values(?,?)", intCol, stringCol);
      assertEquals(2, result.getObject().getId());
      assertEquals(1, result.getRowsModified()[0]);

      // read object and compare with inserted data
      Simple simpleRead = simpleOrMapper
          .readFirst("select * from simple where long_col=? and string_col=?", longCol, stringCol);
      assertNotNull(simpleRead);
      assertEquals(longCol, simpleRead.getLongCol());
      assertEquals(stringCol, simpleRead.getStringCol());

      // delete object and check it was removed
      simpleOrMapper.delete(simpleRead);
      simpleRead = simpleOrMapper
          .readFirst("select * from simple where long_col=? and string_col=?", longCol, stringCol);
      assertNull(simpleRead);
    }
  }

  @Test
  public void testSetAutoGeneratedKeys() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      Simple s = TestSimple.buildSimple();
      InsertResult<Simple> result = simpleOrMapper.insertAndGet(s);
      long id = simpleOrMapper.readFirst(long.class, "select id from simple where long_col=?",
          s.getLongCol());

      assertEquals(id, result.getObject().getId());

      // delete object by primary key and check it was removed
      simpleOrMapper.delete(result.getObject());
      Simple simpleRead = simpleOrMapper.readByPrimaryKey(Simple.class, result.getObject().getId());
      assertNull(simpleRead);
    }
  }

  @Test
  public void testReturnNativeTypes() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);

      Simple simple = buildSimple();
      simpleOrMapper.insert(simple);
      long longCol = simpleOrMapper.readFirst(long.class,
          "select long_col from simple where long_col=?", simple.getLongCol());
      String stringCol = simpleOrMapper.readFirst(String.class,
          "select string_col from simple where long_col=?", simple.getLongCol());

      assertEquals(simple.getLongCol(), longCol);
      assertEquals(simple.getStringCol(), stringCol);
    }
  }

  @Test
  public void testBatch() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);

      Simple simple1 = buildSimple();
      Simple simple2 = buildSimple();
      Simple simple3 = buildSimple();
      simpleOrMapper.insert(List.of(simple1, simple2, simple3));

      List<Simple> list = simpleOrMapper.readAll(Simple.class);
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

      simpleOrMapper.update(List.of(s1, s2, s3));
      list = simpleOrMapper.readAll(Simple.class);
      assertTrue(s.containsAll(list));

      simpleOrMapper.delete(List.of(s1, s2, s3));
      list = simpleOrMapper.readAll(Simple.class);
      assertEquals(0, list.size());
    }
  }

  @Test
  public void testObject() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);

      Simple simpleInsert = buildSimple();
      simpleOrMapper.insert(simpleInsert);

      long id = simpleOrMapper.readFirst(long.class, "select id from simple");

      Simple simpleUpdate = simpleOrMapper.readByPrimaryKey(Simple.class, id);
      assertEquals(simpleInsert, simpleUpdate);

      simpleUpdate.setLongCol(randomLong(0, Integer.MAX_VALUE / 2));
      simpleUpdate.setStringCol(randomString(255));
      simpleOrMapper.update(simpleUpdate);

      Simple simpleRead = simpleOrMapper.readByPrimaryKey(Simple.class, simpleUpdate.getId());
      assertEquals(simpleUpdate, simpleRead);

      simpleOrMapper.delete(simpleRead);

      Simple simpleDeleted = simpleOrMapper.readByPrimaryKey(Simple.class, simpleRead.getId());
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
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);

      Simple simple1 = buildSimple();
      Simple simple2 = buildSimple();
      Simple simple3 = buildSimple();
      simpleOrMapper.insert(simple1);
      simpleOrMapper.insert(simple2);
      simpleOrMapper.insert(simple3);

      List<Simple> list = simpleOrMapper.readAll(Simple.class);
      List<Simple> s = new ArrayList<>();
      s.add(simple1);
      s.add(simple2);
      s.add(simple3);
      assertTrue(s.containsAll(list));

      simpleOrMapper.delete(list.get(0));
      simpleOrMapper.delete(list.get(1));
      simpleOrMapper.delete(list.get(2));
      List<Simple> listDeleted = simpleOrMapper.readAll(Simple.class);
      assertTrue(0 == listDeleted.size());
    }
  }


  @Test
  public void testMap() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);

      Simple simple = buildSimple();
      simpleOrMapper.insert(simple);

      long id = simpleOrMapper.readFirst(long.class, "select id from simple limit 1");

      Map<String, Object> simpleMap1 =
          simpleOrMapper.readMapFirst("select * from simple where id=?", id);
      assertEquals(id, simpleMap1.get("ID"));
      assertEquals(simple.getLongCol(), simpleMap1.get("LONG_COL"));
      assertEquals(simple.getStringCol(), simpleMap1.get("STRING_COL"));

      simpleOrMapper.delete(simple);
    }
  }

  @Test
  public void testMapList() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);

      simpleOrMapper.executeUpdate("DELETE FROM SIMPLE");
      Simple simple1 = buildSimple();
      Simple simple2 = buildSimple();
      Simple simple3 = buildSimple();

      simpleOrMapper.insert(List.of(simple1, simple2, simple3));

      // tests using setAutoUpdateGeneratedKeys do not belong here
      List<Long> ids = simpleOrMapper.readList(long.class, "select id from simple order by id");
      simple1.setId(ids.get(0));
      simple2.setId(ids.get(1));
      simple3.setId(ids.get(2));

      List<Map<String, Object>> simpleList =
          simpleOrMapper.readMapList("select * from simple where id in (?,?,?)", simple1.getId(),
              simple2.getId(), simple3.getId());
      assertEquals(simple1.getId(), simpleList.get(0).get("ID"));
      assertEquals(simple1.getLongCol(), simpleList.get(0).get("LONG_COL"));
      assertEquals(simple1.getStringCol(), simpleList.get(0).get("STRING_COL"));

      assertEquals(simple2.getId(), simpleList.get(1).get("ID"));
      assertEquals(simple2.getLongCol(), simpleList.get(1).get("LONG_COL"));
      assertEquals(simple2.getStringCol(), simpleList.get(1).get("STRING_COL"));

      assertEquals(simple3.getId(), simpleList.get(2).get("ID"));
      assertEquals(simple3.getLongCol(), simpleList.get(2).get("LONG_COL"));
      assertEquals(simple3.getStringCol(), simpleList.get(2).get("STRING_COL"));

      simpleOrMapper.delete(simple1);
      simpleOrMapper.delete(simple2);
      simpleOrMapper.delete(simple3);
    }
  }


  @Test
  public void testMappingSimple01() {
    // Simple01 specifies an invalid column name but field is used.
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      long id = simpleOrMapper.readFirst(long.class, "select id from simple limit 1");
      simpleOrMapper.readByPrimaryKey(Simple01.class, id);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testMappingSimple02() {
    // Simple02 specifies an invalid table
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      long id = simpleOrMapper.readFirst(long.class, "select id from simple limit 1");
      simpleOrMapper.readByPrimaryKey(Simple02.class, id);
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any existing table");

    }
  }

  @Test
  public void testMappingSimple03() {
    // Simple03 lacks a field
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      simpleOrMapper.readByPrimaryKey(Simple03.class, 1);
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("[LONG_COL] does not match any field");
    }
  }


  @Test
  public void testMappingSimple04() {
    // Simple04 has incompatible setter
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      Simple04 ret = simpleOrMapper.readByPrimaryKey(Simple04.class, 1);
      fail("Object with incompatible getter and setter did not trigger exception");
    } catch (Exception e) {
      org.assertj.core.api.Assertions.assertThat(e.getCause().getMessage())
          .contains("Could not set a value");
    }
  }


  @Test
  public void testMappingSimple05() {
    // Simple05 doesn't specify a table name and guessed names won't work
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      simpleOrMapper.readByPrimaryKey(Simple05.class, 1);
      fail("Object with invalid table name did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any existing table");
    }
  }

  @Test
  public void testMappingSimple06() {
    // Simple06 has different annotations for getter and setter
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      simpleOrMapper.readByPrimaryKey(Simple06.class, 1);
      fail("Object without getter and setter did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any field");
    }
  }

  @Test
  public void testMappingSimple07() {
    // Simple07 doesn't have a getter and setter for string_col
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      simpleOrMapper.readByPrimaryKey(Simple07.class, 1);
      fail("Object without getter and setter did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("[STRING_COL] does not match any field");
    }
  }

  @Test
  public void testMappingSimple08() {
    // Simple07 doesn't have a getter and setter for string_col
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      simpleOrMapper.readByPrimaryKey(Simple08.class, 1);
      fail("Object with conflicting annotations did not trigger exception");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any field");
    }
  }

  @Test
  public void testMappingSimple10() {
    // Simple10 has setter with no parameters
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.readByPrimaryKey(Simple10.class, 1);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testMappingSimple11() {
    // Simple10 has setter with no parameters
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(new Simple11());
    } catch (Exception e) {
      assertThat(e.getCause().getMessage()).contains("does not match any field");
    }
  }

  @Test
  public void testMappingSimple12() {
    // Simple10 has setter with no parameters
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(new Simple12(1));
      simpleOrMapper.readAll(Simple12.class);
    } catch (Exception e) {
      assertThat(e.getCause().getMessage()).contains("default constructor");
    }
  }

  @Test
  public void testMapping() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper simpleOrMapper = Sorm.getOrmConnection(conn);
      simpleOrMapper.insert(buildSimple());
      long id = simpleOrMapper.readFirst(long.class, "select id from simple limit 1");
      // Simple09 has getter which returns void
      try {
        simpleOrMapper.readByPrimaryKey(Simple09.class, id);
        fail("Object with getter returning void did not trigger exception");
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("does not match any field");
      }
    }
  }

  @Test
  public void TestUpperSnakeCase() {
    Set<String> guessed = Set.of(StringUtils.toUpperSnakeCase("name"));
    Set<String> expected = Set.of("NAME");
    assertEquals(expected, guessed);

    guessed = Set.of(StringUtils.toUpperSnakeCase("nameC"));
    expected = Set.of("NAME_C");

    assertEquals(expected, guessed);

    guessed = Set.of(StringUtils.toUpperSnakeCase("nameCo"));
    expected = Set.of("NAME_CO");
    assertEquals(expected, guessed);

    guessed = Set.of(StringUtils.toUpperSnakeCase("n"));
    expected = Set.of("N");

    assertEquals(expected, guessed);

    guessed = Set.of(StringUtils.toUpperSnakeCase("nC"));
    expected = Set.of("N_C");
    assertEquals(expected, guessed);

    guessed = Set.of(StringUtils.toUpperSnakeCase("nCMP"));
    expected = Set.of("N_C_M_P");
    assertEquals(expected, guessed);

  }


}
