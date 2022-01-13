package org.nkjmlab.sorm4j.mapping;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.mapping.FieldAccessor;

class AccessorTest {

  @Test
  void testAccessor() {
    FieldAccessor ac = new FieldAccessor(null, null, null, null);
    System.out.println(ac.toString());
  }

}
