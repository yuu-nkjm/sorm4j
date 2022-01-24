package org.nkjmlab.sorm4j.context;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.SormException;

class AccessorTest {

  @Test
  void testAccessor() {
    FieldAccessor ac = new FieldAccessor(null, null, null, null);

    assertThrows(SormException.class, () -> ac.get(1));
  }

}
