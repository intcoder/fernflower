// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler;

import org.jetbrains.java.decompiler.code.CodeConstants;
import org.jetbrains.java.decompiler.main.extern.IIdentifierRenamer.Type;
import org.jetbrains.java.decompiler.modules.renamer.ConverterHelper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConverterHelperTest {
  private static final String VALID_CLASS_NAME = "ValidClassName";
  private static final String VALID_FIELD_NAME = "validFieldName";
  private static final String VALID_METHOD_NAME = "validMethodName";
  private static final String VALID_FIELD_DESCRIPTOR = "I";
  private static final String VALID_METHOD_DESCRIPTOR = "()V";

  @Test void testValidClassName() { doTestClassName(VALID_CLASS_NAME, false); }
  @Test void testValidFieldName() { doTestFieldName(VALID_FIELD_NAME, VALID_FIELD_DESCRIPTOR, false); }
  @Test void testValidMethodName() { doTestMethodName(VALID_METHOD_NAME, VALID_METHOD_DESCRIPTOR, false); }

  @Test void testNullClassName() { doTestClassName(null, true); }
  @Test void testNullFieldName() { doTestFieldName(null, VALID_FIELD_DESCRIPTOR, true); }
  @Test void testNullMethodName() { doTestMethodName(null, VALID_METHOD_DESCRIPTOR, true); }

  @Test void testEmptyClassName() { doTestClassName("", true); }
  @Test void testEmptyFieldName() { doTestFieldName("", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testEmptyMethodName() { doTestMethodName("", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testShortClassName() { doTestClassName("C", true); }
  @Test void testShortFieldName() { doTestFieldName("f", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testShortMethodName() { doTestMethodName("m", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testUnderscoreClassName() { doTestClassName("_", true); }
  @Test void testUnderscoreFieldName() { doTestFieldName("_", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testUnderscoreMethodName() { doTestMethodName("_", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testKeywordClassName() { doTestClassName("public", true); }
  @Test void testKeywordFieldName() { doTestFieldName("public", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testKeywordMethodName() { doTestMethodName("public", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testReservedWindowsNamespaceClassName() { doTestClassName("nul", true); }
  @Test void testReservedWindowsNamespaceFieldName() { doTestFieldName("nul", VALID_FIELD_DESCRIPTOR, false); }
  @Test void testReservedWindowsNamespaceName() { doTestMethodName("nul", VALID_METHOD_DESCRIPTOR, false); }

  @Test void testLeadingDigitClassName() { doTestClassName("4identifier", true); }
  @Test void testLeadingDigitFieldName() { doTestFieldName("4identifier", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testLeadingDigitMethodName() { doTestMethodName("4identifier", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testInvalidLeadingCharClassName() { doTestClassName("\uFEFFClassName", true); }
  @Test void testInvalidLeadingCharFieldName() { doTestFieldName("\uFEFFfieldName", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testInvalidLeadingCharMethodName() { doTestMethodName("\uFEFFmethodName", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testInvalidMiddleCharClassName() { doTestClassName("Class\uFEFFName", true); }
  @Test void testInvalidMiddleCharFieldName() { doTestFieldName("field\uFEFFName", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testInvalidMiddleCharMethodName() { doTestMethodName("method\uFEFFName", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testInvalidTrailingCharClassName() { doTestClassName("ClassName\uFEFF", true); }
  @Test void testInvalidTrailingCharFieldName() { doTestFieldName("fieldName\uFEFF", VALID_FIELD_DESCRIPTOR, true); }
  @Test void testInvalidTrailingCharMethodName() { doTestMethodName("methodName\uFEFF", VALID_METHOD_DESCRIPTOR, true); }

  @Test void testLtInitGtClassName() { doTestClassName(CodeConstants.INIT_NAME, true); }
  @Test void testLtInitGtFieldName() { doTestFieldName(CodeConstants.INIT_NAME, VALID_FIELD_DESCRIPTOR, true); }
  @Test void testLtInitGtMethodName() { doTestMethodName(CodeConstants.INIT_NAME, VALID_METHOD_DESCRIPTOR, false); }

  @Test void testLtClinitGtClassName() { doTestClassName(CodeConstants.CLINIT_NAME, true); }
  @Test void testLtClinitGtFieldName() { doTestFieldName(CodeConstants.CLINIT_NAME, VALID_FIELD_DESCRIPTOR, true); }
  @Test void testLtClinitGtMethodName() { doTestMethodName(CodeConstants.CLINIT_NAME, VALID_METHOD_DESCRIPTOR, false); }

  private static void doTestClassName(String className, boolean shallBeRenamed) {
    doTest(Type.ELEMENT_CLASS, className, null, null, shallBeRenamed);
  }

  private static void doTestFieldName(String element, String descriptor, boolean shallBeRenamed) {
    doTest(Type.ELEMENT_FIELD, VALID_CLASS_NAME, element, descriptor, shallBeRenamed);
  }

  private static void doTestMethodName(String element, String descriptor, boolean shallBeRenamed) {
    doTest(Type.ELEMENT_METHOD, VALID_CLASS_NAME, element, descriptor, shallBeRenamed);
  }

  private static void doTest(Type elementType, String className, String element, String descriptor, boolean shallBeRenamed) {
    boolean result = new ConverterHelper().toBeRenamed(elementType, className, element, descriptor);
    String assertionMessage = shallBeRenamed ? "Identifier { %s, %s, %s, %s } shall be renamed" : "Identifier { %s, %s, %s, %s } shall not be renamed";

    assertTrue(result == shallBeRenamed, String.format(assertionMessage, elementType.toString(), className, element, descriptor));
  }
}