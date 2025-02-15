package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.context.SormContext;

class StringCacheTest {
  @Test
  void testToCanonicalName() {

    Map<String, String> map =
        Map.ofEntries(
            Map.entry("User Name (USD)", "USER_NAME_USD"),
            Map.entry("Café Déjà-vu!", "CAFÉ_DÉJÀ_VU"),
            Map.entry("商品価格（円）", "商品価格_円"),
            Map.entry("data-set#1", "DATA_SET_1"),
            Map.entry("Test__Value__", "TEST_VALUE"),
            Map.entry("ユーザ　ID", "ユーザ_ID"),
            Map.entry("USER___ID", "USER_ID"),
            Map.entry("userId", "USER_ID"),
            Map.entry("httpRequest", "HTTP_REQUEST"),
            Map.entry("foo123Bar", "FOO123_BAR"),
            Map.entry("fooBar123", "FOO_BAR123"),
            Map.entry("id123ABC", "ID123_ABC"),
            Map.entry("xmlHttpRequest", "XML_HTTP_REQUEST"),
            Map.entry("parseURL123", "PARSE_URL123"),
            Map.entry("httpStatusCode", "HTTP_STATUS_CODE"),
            Map.entry("loadPage2Data", "LOAD_PAGE2_DATA"),
            Map.entry("priceInUSD", "PRICE_IN_USD"),
            Map.entry("UserId", "USER_ID"),
            Map.entry("HttpRequest", "HTTP_REQUEST"),
            Map.entry("Foo123Bar", "FOO123_BAR"),
            Map.entry("FooBar123", "FOO_BAR123"),
            Map.entry("Id123ABC", "ID123_ABC"),
            Map.entry("XmlHttpRequest", "XML_HTTP_REQUEST"),
            Map.entry("ParseURL123", "PARSE_URL123"),
            Map.entry("HttpStatusCode", "HTTP_STATUS_CODE"),
            Map.entry("LoadPage2Data", "LOAD_PAGE2_DATA"),
            Map.entry("PriceInUSD", "PRICE_IN_USD"),
            Map.entry("USER_ID", "USER_ID"),
            Map.entry("HTTP_REQUEST", "HTTP_REQUEST"),
            Map.entry("FOO123_BAR", "FOO123_BAR"),
            Map.entry("FOO_BAR123", "FOO_BAR123"),
            Map.entry("ID123_ABC", "ID123_ABC"),
            Map.entry("XML_HTTP_REQUEST", "XML_HTTP_REQUEST"),
            Map.entry("PARSE_URL123", "PARSE_URL123"),
            Map.entry("HTTP_STATUS_CODE", "HTTP_STATUS_CODE"),
            Map.entry("LOAD_PAGE2_DATA", "LOAD_PAGE2_DATA"),
            Map.entry("PRICE_IN_USD", "PRICE_IN_USD"),
            Map.entry("User ID", "USER_ID"),
            Map.entry("HTTP Request", "HTTP_REQUEST"),
            Map.entry("Foo-123-Bar", "FOO_123_BAR"),
            Map.entry("Foo.Bar.123", "FOO_BAR_123"),
            Map.entry("Xml/Http\\Request", "XML_HTTP_REQUEST"),
            Map.entry("Parse.URL.123", "PARSE_URL_123"),
            Map.entry("Http Status Code", "HTTP_STATUS_CODE"),
            Map.entry("合格A", "合格A"),
            Map.entry("データID", "データID"),
            Map.entry("得点率%", "得点率"),
            Map.entry("ユーザー名", "ユーザー名"));

    map.keySet()
        .forEach(
            src ->
                assertThat(SormContext.getDefaultCanonicalStringCache().toCanonicalName(src))
                    .isEqualTo(map.get(src)));
  }
}
