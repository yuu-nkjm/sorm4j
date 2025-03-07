package org.nkjmlab.sorm4j.extension.datatype.container;

import java.nio.charset.StandardCharsets;

public class JsonByte {

  private final byte[] bytes;

  private JsonByte(byte[] jsonBytes) {
    this.bytes = jsonBytes;
  }

  private JsonByte(String jsonString) {
    this.bytes = jsonString.getBytes(StandardCharsets.UTF_8);
  }

  public byte[] getBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    return new String(bytes, StandardCharsets.UTF_8);
  }

  public static JsonByte of(String jsonString) {
    return new JsonByte(jsonString);
  }

  public static JsonByte of(byte[] jsonBytes) {
    return new JsonByte(jsonBytes);
  }
}
