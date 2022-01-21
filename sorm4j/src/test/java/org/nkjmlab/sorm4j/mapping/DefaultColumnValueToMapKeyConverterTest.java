package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.mapping.DefaultColumnValueToMapEntryConverter.LetterCaseOfKey;

class DefaultColumnValueToMapKeyConverterTest {

  @Test
  void testConvertToKey() {
    String key = "keY_Name";
    assertThat(
        new DefaultColumnValueToMapKeyConverter(LetterCaseOfKey.CANONICAL_CASE).convertToKey(key))
            .isEqualTo("KEYNAME");
    assertThat(
        new DefaultColumnValueToMapKeyConverter(LetterCaseOfKey.LOWER_CASE).convertToKey(key))
            .isEqualTo("key_name");
    assertThat(
        new DefaultColumnValueToMapKeyConverter(LetterCaseOfKey.UPPER_CASE).convertToKey(key))
            .isEqualTo("KEY_NAME");
    assertThat(
        new DefaultColumnValueToMapKeyConverter(LetterCaseOfKey.NO_CONVERSION).convertToKey(key))
            .isEqualTo("keY_Name");
  }

}
