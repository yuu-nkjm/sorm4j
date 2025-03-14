package org.nkjmlab.sorm4j.extension.datatype.jts;

import org.nkjmlab.sorm4j.extension.datatype.SupportTypes;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

class JtsSupportTypes implements SupportTypes {

  @Override
  public boolean isSupport(Class<?> toType) {
    return JtsGeometry.class.equals(toType)
        || JtsGeometry.class.equals(ArrayUtils.getInternalComponentType(toType));
  }
}
