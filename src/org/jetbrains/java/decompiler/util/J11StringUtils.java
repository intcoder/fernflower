package org.jetbrains.java.decompiler.util;

import org.apache.commons.lang3.StringUtils;

public class J11StringUtils {

  public static String repeat(final String str, final int repeat) {
    if (repeat < 0) {
      throw new IllegalArgumentException("count is negative: " + repeat);
    }

    return StringUtils.repeat(str, repeat);
  }
}
