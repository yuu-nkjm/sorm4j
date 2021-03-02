package org.nkjmlab.sorm4j.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PerformanceH2Test {
  PerformanceH2 env;

  @BeforeEach
  void setUp() {
    this.env = new PerformanceH2("mem");
  }

  @Test
  void test() {
    env.run();
  }

}
