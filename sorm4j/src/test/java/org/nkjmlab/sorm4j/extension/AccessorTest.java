package org.nkjmlab.sorm4j.extension;

import org.junit.jupiter.api.Test;

class AccessorTest {

  @Test
  void testAccessor() {
    Accessor ac = new Accessor(null, null, null, null);
    System.out.println(ac.toString());
  }

}
