package org.nkjmlab.sorm4j.extension.datatype.jts;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.DataTypeSupport;

/**
 * Provides support for handling spatial data types in SORM (Simple Object Relational Mapping) by
 * integrating JTS (Java Topology Suite).
 *
 * <p>This class registers custom converters to store and retrieve spatial data, such as geometries,
 * in database columns.
 *
 * @author yuu_nkjm
 */
public class JtsSupport implements DataTypeSupport {

  /**
   * Registers JTS-based converters to handle spatial data in SORM.
   *
   * @param builder the {@link SormContext.Builder} to which the spatial data support is added
   * @return the updated {@link SormContext.Builder} instance
   */
  @Override
  public SormContext.Builder addSupport(SormContext.Builder builder) {
    return builder
        .addColumnValueToJavaObjectConverter(new JtsColumnValueToJavaObjectConverter())
        .addSqlParameterSetter(new JtsSqlParameterSetter());
  }
}
