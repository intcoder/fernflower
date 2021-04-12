//package org.jetbrains.java.decompiler.util;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class J11StringUtilsTest {
//
//  @Test
//  void repeat() {
//    for (int i = 0; i < 10; i++) {
//      assertEquals("[".repeat(i), J11StringUtils.repeat("[", i));
//    }
//
//    for (int i = -1; i > -10; i--) {
//      int i0 = i; // for lambdas
//      assertThrows(IllegalArgumentException.class, () -> "[".repeat(i0));
//      assertThrows(IllegalArgumentException.class, () -> J11StringUtils.repeat("[", i0));
//    }
//  }
//}