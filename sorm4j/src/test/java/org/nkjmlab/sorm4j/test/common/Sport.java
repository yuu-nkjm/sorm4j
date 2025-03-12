package org.nkjmlab.sorm4j.test.common;

/** Results record container */
public record Sport(int id, Sports name) {

  public enum Sports {
    TENNIS,
    SOCCER
  }
}
