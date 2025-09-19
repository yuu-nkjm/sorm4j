package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.context.SormContext;

class StringCacheTest {
  @Test
  void test() {
    SormContext.getDefaultCanonicalStringCache().toCanonicalName("user");
    assertThat(SormContext.getDefaultCanonicalStringCache().getCache().containsKey("user"))
        .isTrue();
    assertThat(SormContext.getDefaultCanonicalStringCache().toString()).isNotNull();
  }

  @Test
  void testToCanonicalName() {

    Map<String, String> map =
        Map.ofEntries(
            Map.entry("User Name (USD)", "USER_NAME_USD"),
            Map.entry("Café Déjà-vu!", "CAFÉ_DÉJÀ_VU"),
            Map.entry("価格（円）", "価格_円"),
            Map.entry("data-set#1", "DATA_SET_1"),
            Map.entry("Test__Value__", "TEST_VALUE"),
            Map.entry("ユーザ　ID", "ユーザ_ID"),
            Map.entry("USER___ID", "USER_ID"),
            Map.entry("userId", "USER_ID"),
            Map.entry("httpRequest", "HTTP_REQUEST"),
            Map.entry("foo123Bar", "FOO_123_BAR"),
            Map.entry("fooBar123", "FOO_BAR_123"),
            Map.entry("id123ABC", "ID_123_ABC"),
            Map.entry("xmlHttpRequest", "XML_HTTP_REQUEST"),
            Map.entry("parseURL123", "PARSE_URL_123"),
            Map.entry("httpStatusCode", "HTTP_STATUS_CODE"),
            Map.entry("loadPage2Data", "LOAD_PAGE_2_DATA"),
            Map.entry("priceInUSD", "PRICE_IN_USD"),
            Map.entry("UserId", "USER_ID"),
            Map.entry("HttpRequest", "HTTP_REQUEST"),
            Map.entry("Foo123Bar", "FOO_123_BAR"),
            Map.entry("FooBar123", "FOO_BAR_123"),
            Map.entry("Id123ABC", "ID_123_ABC"),
            Map.entry("XmlHttpRequest", "XML_HTTP_REQUEST"),
            Map.entry("ParseURL123", "PARSE_URL_123"),
            Map.entry("HttpStatusCode", "HTTP_STATUS_CODE"),
            Map.entry("LoadPage2Data", "LOAD_PAGE_2_DATA"),
            Map.entry("PriceInUSD", "PRICE_IN_USD"),
            Map.entry("USER_ID", "USER_ID"),
            Map.entry("HTTP_REQUEST", "HTTP_REQUEST"),
            Map.entry("FOO123_BAR", "FOO_123_BAR"),
            Map.entry("FOO_BAR123", "FOO_BAR_123"),
            Map.entry("ID123_ABC", "ID_123_ABC"),
            Map.entry("XML_HTTP_REQUEST", "XML_HTTP_REQUEST"),
            Map.entry("PARSE_URL123", "PARSE_URL_123"),
            Map.entry("HTTP_STATUS_CODE", "HTTP_STATUS_CODE"),
            Map.entry("LOAD_PAGE2_DATA", "LOAD_PAGE_2_DATA"),
            Map.entry("PRICE_IN_USD", "PRICE_IN_USD"),
            Map.entry("User ID", "USER_ID"),
            Map.entry("HTTP Request", "HTTP_REQUEST"),
            Map.entry("Foo-123-Bar", "FOO_123_BAR"),
            Map.entry("Foo.Bar.123", "FOO_BAR_123"),
            Map.entry("Xml/Http\\Request", "XML_HTTP_REQUEST"),
            Map.entry("Parse.URL.123", "PARSE_URL_123"),
            Map.entry("Http Status Code", "HTTP_STATUS_CODE"),
            Map.entry("ユーザA", "ユーザA"),
            Map.entry("ユーザID", "ユーザID"),
            Map.entry("価格%", "価格"),
            Map.entry("ユーザー名", "ユーザー名"));

    map.keySet()
        .forEach(
            src ->
                assertThat(SormContext.getDefaultCanonicalStringCache().toCanonicalName(src))
                    .isEqualTo(map.get(src)));
  }

  @Test
  void testToPascalCase_basic() {
    assertEquals("HttpRequest", CanonicalStringUtils.toPascalCase("HTTP_REQUEST"));
    assertEquals("UserName", CanonicalStringUtils.toPascalCase("USER_NAME"));
    assertEquals("StudentId", CanonicalStringUtils.toPascalCase("STUDENT_ID"));
  }

  @Test
  void testToCamelCase_basic() {
    assertEquals("httpRequest", CanonicalStringUtils.toCamelCase("HTTP_REQUEST"));
    assertEquals("userName", CanonicalStringUtils.toCamelCase("USER_NAME"));
    assertEquals("studentId", CanonicalStringUtils.toCamelCase("STUDENT_ID"));
  }

  @Test
  void testHandlesLowerSnakeInput() {
    assertEquals("HttpRequest", CanonicalStringUtils.toPascalCase("http_request"));
    assertEquals("httpRequest", CanonicalStringUtils.toCamelCase("http_request"));
  }

  @Test
  void testHandlesMixedCaseInput() {
    assertEquals("HttpRequest", CanonicalStringUtils.toPascalCase("Http_Request"));
    assertEquals("httpRequest", CanonicalStringUtils.toCamelCase("Http_Request"));
  }

  @Test
  void testConsecutiveUnderscoresAreSkipped() {
    assertEquals("HttpRequest", CanonicalStringUtils.toPascalCase("HTTP__REQUEST"));
    assertEquals("httpRequest", CanonicalStringUtils.toCamelCase("HTTP__REQUEST"));
  }

  @Test
  void testEmptyOrNull() {
    assertEquals("", CanonicalStringUtils.toPascalCase(""));
    assertEquals("", CanonicalStringUtils.toCamelCase(""));
    assertEquals(null, CanonicalStringUtils.toPascalCase(null));
    assertEquals(null, CanonicalStringUtils.toCamelCase(null));
  }
}
