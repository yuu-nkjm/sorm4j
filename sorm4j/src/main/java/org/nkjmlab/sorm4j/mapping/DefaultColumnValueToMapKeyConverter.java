package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.mapping.DefaultColumnValueToMapEntryConverter.LetterCaseOfKey;

public final class DefaultColumnValueToMapKeyConverter implements ColumnValueToMapKeyConverter {

  @Experimental
  private final LetterCaseOfKey letterCaseOfKey;

  public DefaultColumnValueToMapKeyConverter(LetterCaseOfKey letterCaseOfKey) {
    this.letterCaseOfKey = letterCaseOfKey;
  }

  @Override
  public String convertToKey(String columnName) {
    return convertLetterCase(letterCaseOfKey, columnName);
  }

  private static String convertLetterCase(LetterCaseOfKey letterCaseOfKey, String columnName) {
    switch (letterCaseOfKey) {
      case LOWER_CASE:
        return toLowerCase(columnName);
      case UPPER_CASE:
        return toUpperCase(columnName);
      case CANONICAL_CASE:
        return toCanonicalCase(columnName);
      case NO_CONVERSION:
        return columnName;
      default:
        throw new IllegalArgumentException(
            ParameterizedStringUtils.newString("{} is invalid", letterCaseOfKey));
    }
  }

}
