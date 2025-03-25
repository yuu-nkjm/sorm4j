package org.nkjmlab.sorm4j.extension.datatype;

import org.nkjmlab.sorm4j.context.SormContext;

/**
 * Defines an interface for extending data type support in SORM (Simple Object Relational Mapping).
 *
 * <p>Implementations of this interface provide additional data type handling by registering custom
 * converters and parameter setters within the {@link SormContext}.
 *
 * @author yuu_nkjm
 */
public interface DataTypeSupport {

  /**
   * Adds custom data type support to the given {@link SormContext.Builder}.
   *
   * @param contextBuilder the {@link SormContext.Builder} to which the data type support is added
   * @return the updated {@link SormContext.Builder} instance
   */
  SormContext.Builder addSupport(SormContext.Builder contextBuilder);
}
