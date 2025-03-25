package org.nkjmlab.sorm4j.extension.datatype.container;

import java.nio.charset.StandardCharsets;

/**
 * A container class for handling JSON data in byte array format. This class provides methods for
 * creating instances from both JSON strings and byte arrays.
 *
 * <p>Instances of this class are immutable and provide a {@code toString()} method that converts
 * the stored byte array back to a UTF-8 encoded JSON string.
 */
public record JsonByte(byte[] bytes) {

  /**
   * Constructs a {@code JsonByte} instance from a JSON string.
   *
   * @param jsonString the JSON string to be converted into a byte array.
   */
  private JsonByte(String jsonString) {
    this(jsonString.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Returns the JSON byte array as a UTF-8 encoded string.
   *
   * @return the JSON string representation of the byte array.
   */
  @Override
  public String toString() {
    return new String(bytes, StandardCharsets.UTF_8);
  }

  /**
   * Creates a new {@code JsonByte} instance from a JSON string.
   *
   * @param jsonString the JSON string to be stored.
   * @return a new instance of {@code JsonByte}.
   */
  public static JsonByte of(String jsonString) {
    return new JsonByte(jsonString);
  }

  /**
   * Creates a new {@code JsonByte} instance from a byte array.
   *
   * @param jsonBytes the JSON data in byte array format.
   * @return a new instance of {@code JsonByte}.
   */
  public static JsonByte of(byte[] jsonBytes) {
    return new JsonByte(jsonBytes);
  }
}
