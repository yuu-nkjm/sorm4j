package org.nkjmlab.sorm4j.extension.jts;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.container.GeometryString;
import org.nkjmlab.sorm4j.extension.datatype.jts.GeometryJts;
import org.nkjmlab.sorm4j.extension.datatype.jts.JtsSupport;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2DefinedTable;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecordCompatible;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class GeometryTest {

  @Test
  void stringTest() {

    Sorm sorm = Sorm.create(SormTestUtils.createNewDatabaseDataSource());

    H2DefinedTable<GeometryStringRecord> table =
        H2DefinedTable.of(sorm, GeometryStringRecord.class);
    assertThat(table.getTableDefinition().getCreateTableIfNotExistsStatement())
        .contains("GEO_STR geometry");
    table.createTableIfNotExists();
    GeometryString g = GeometryString.of("POINT (30 10)");
    GeometryString g1 = GeometryString.of("POINT (30 10)");

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
        Sorm.create(
            SormTestUtils.createNewDatabaseDataSource(),
            new JtsSupport().addSupport(SormContext.builder()).build());

    H2DefinedTable<GeometryJtsRecord> table = H2DefinedTable.of(sorm, GeometryJtsRecord.class);
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

  @OrmRecordCompatible
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

  @OrmRecordCompatible
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
