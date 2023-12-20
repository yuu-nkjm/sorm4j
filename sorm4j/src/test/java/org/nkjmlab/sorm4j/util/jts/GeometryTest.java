package org.nkjmlab.sorm4j.util.jts;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.datatype.GeometryString;
import org.nkjmlab.sorm4j.util.h2.H2BasicTable;

class GeometryTest {

  @Test
  void stringTest() {

    Sorm sorm = Sorm.create(SormTestUtils.createNewDatabaseDataSource());

    H2BasicTable<GeometryStringRecord> table = new H2BasicTable<>(sorm, GeometryStringRecord.class);
    assertThat(table.getTableDefinition().getCreateTableIfNotExistsStatement())
        .contains("GEO_STR geometry");
    table.createTableIfNotExists();
    GeometryString g = new GeometryString("POINT (30 10)");
    GeometryString g1 = new GeometryString("POINT (30 10)");

    assertThat(g.equals(g1)).isTrue();
    assertThat(g.hashCode() == g1.hashCode()).isTrue();
    assertThat(g.toString()).isEqualTo("POINT (30 10)");

    table.insert(new GeometryStringRecord(g));

    GeometryString ret =
        table
            .getOrm()
            .readFirst(GeometryString.class, "select geo_str from geometry_string_records");

    assertThat(ret).isEqualTo(g);
  }

  @Test
  void jtsTest() {

    Sorm sorm =
        Sorm.create(SormTestUtils.createNewDatabaseDataSource(), JtsSormContext.builder().build());

    H2BasicTable<GeometryJtsRecord> table = new H2BasicTable<>(sorm, GeometryJtsRecord.class);
    assertThat(table.getTableDefinition().getCreateTableIfNotExistsStatement())
        .contains("GEO_JTS geometry");
    table.createTableIfNotExists();

    GeometryFactory factory = new GeometryFactory();
    Point coordinate = factory.createPoint(new Coordinate(100, 200));
    GeometryJts g = new GeometryJts(coordinate);
    GeometryJts g1 = new GeometryJts(coordinate);
    assertThat(g.equals(g1)).isTrue();
    assertThat(g.hashCode() == g1.hashCode()).isTrue();
    assertThat(g.toString()).isEqualTo("POINT (100 200)");

    table.insert(new GeometryJtsRecord(g));

    GeometryJts ret =
        table.getOrm().readFirst(GeometryJts.class, "select geo_jts from geometry_jts_records");
    assertThat(ret).isEqualTo(g);
  }

  @OrmRecord
  public static class GeometryStringRecord {

    public final GeometryString geoStr;

    public GeometryStringRecord(GeometryString geoStr) {
      this.geoStr = geoStr;
    }

    @Override
    public String toString() {
      return "GeometryJtsRecord [geoJts=" + geoStr + "]";
    }
  }

  @OrmRecord
  public static class GeometryJtsRecord {

    public final GeometryJts geoJts;

    public GeometryJtsRecord(GeometryJts geoJts) {
      this.geoJts = geoJts;
    }

    @Override
    public String toString() {
      return "GeometryJtsRecord [geoJts=" + geoJts + "]";
    }
  }
}
