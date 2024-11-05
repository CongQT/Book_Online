package com.example.bookreadingonline.util;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;

import static com.google.common.base.CaseFormat.*;

public class MyStringUtils {

  public static String normalize(String s) {
    return Normalizer.normalize(s, Normalizer.Form.NFC);
  }

  public static CaseFormat getCaseFormat(String s) {
    if (s.matches("[_a-z0-9]*")) {
      return LOWER_UNDERSCORE;
    } else if (s.matches("[_A-Z0-9]*")) {
      return UPPER_UNDERSCORE;
    } else if (s.matches("[-a-z0-9]*")) {
      return LOWER_HYPHEN;
    } else if (s.matches("[a-z0-9]+([A-Z][a-z0-9]*)*")) {
      return LOWER_CAMEL;
    } else if (s.matches("([A-Z][a-z0-9]*)*")) {
      return UPPER_CAMEL;
    }
    return null;
  }

  public static String toCaseFormat(String s, CaseFormat caseFormat) {
    return getCaseFormat(s).to(caseFormat, s);
  }

  public static String toCamelCase(String s) {
    return toCaseFormat(s, LOWER_CAMEL);
  }

  public static String escapeSqlWildcard(String s) {
    if (s != null) {
      return s.replace("!", "!!")
          .replace("%", "!%")
          .replace("_", "!_")
          .replace("[", "![");
    } else {
      return null;
    }
  }

  public static String buildSqlLikePattern(String s) {
    String escapedString = StringUtils.trimToNull(escapeSqlWildcard(s));
    return escapedString != null ? "%" + escapedString.toLowerCase() + "%" : null;
  }

  public static String extractUsernameFromEmail(String email) {
    return email.replaceAll("@.*", "").toLowerCase();
  }

}
