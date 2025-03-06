package org.nkjmlab.sorm4j.extension.datatype.jts;

import org.nkjmlab.sorm4j.extension.datatype.SupportTypes;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;

class JtsSupportTypes implements SupportTypes {

  @Override
  public boolean isSupport(Class<?> toType) {
    return GeometryJts.class.equals(toType)
        || GeometryJts.class.equals(ArrayUtils.getInternalComponentType(toType));
  }
}
