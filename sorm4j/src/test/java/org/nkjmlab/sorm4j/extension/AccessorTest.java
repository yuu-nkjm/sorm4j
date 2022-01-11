package org.nkjmlab.sorm4j.extension;

import org.junit.jupiter.api.Test;

class AccessorTest {

  @Test
  void testAccessor() {
    FieldAccessor ac = new FieldAccessor(null, null, null, null);
    System.out.println(ac.toString());
  }

}
