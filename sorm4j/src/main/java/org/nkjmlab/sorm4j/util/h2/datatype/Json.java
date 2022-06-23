package org.nkjmlab.sorm4j.util.h2.datatype;

import java.nio.charset.StandardCharsets;

public class Json {

  private final byte[] bytes;

  public Json(byte[] bytes) {
    this.bytes = bytes;
  }

  public byte[] getBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    return new String(bytes, StandardCharsets.UTF_8);
  }

}
