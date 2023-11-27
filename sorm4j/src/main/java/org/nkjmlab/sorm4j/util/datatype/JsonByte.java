package org.nkjmlab.sorm4j.util.datatype;

import java.nio.charset.StandardCharsets;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public class JsonByte {

  private final byte[] bytes;

  public JsonByte(byte[] jsonBytes) {
    this.bytes = jsonBytes;
  }

  public JsonByte(String jsonString) {
    this.bytes = jsonString.getBytes(StandardCharsets.UTF_8);
  }

  public byte[] getBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
