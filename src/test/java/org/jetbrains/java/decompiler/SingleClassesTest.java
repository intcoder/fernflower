// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jetbrains.java.decompiler.DecompilerTestFixture.assertFilesEqual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DecTestFixtureExtension.class)
class SingleClassesTest {

  @DecTestFixture({IFernflowerPreferences.BYTECODE_SOURCE_MAPPING, "1",
    IFernflowerPreferences.DUMP_ORIGINAL_LINES, "1",
    IFernflowerPreferences.IGNORE_INVALID_BYTECODE, "1",
    IFernflowerPreferences.VERIFY_ANONYMOUS_CLASSES, "1"})
  private DecompilerTestFixture fixture;

  @Test void testPrimitiveNarrowing() { doTest("pkg/TestPrimitiveNarrowing"); }
  @Test void testClassFields() { doTest("pkg/TestClassFields"); }
  @Test void testInterfaceFields() { doTest("pkg/TestInterfaceFields"); }
  @Test void testClassLambda() { doTest("pkg/TestClassLambda"); }
  @Test void testClassLoop() { doTest("pkg/TestClassLoop"); }
  @Test void testClassSwitch() { doTest("pkg/TestClassSwitch"); }
  @Test void testClassTypes() { doTest("pkg/TestClassTypes"); }
  @Test void testClassVar() { doTest("pkg/TestClassVar"); }
  @Test void testClassNestedInitializer() { doTest("pkg/TestClassNestedInitializer"); }
  @Test void testClassCast() { doTest("pkg/TestClassCast"); }
  @Test void testDeprecations() { doTest("pkg/TestDeprecations"); }
  @Test void testExtendsList() { doTest("pkg/TestExtendsList"); }
  @Test void testMethodParameters() { doTest("pkg/TestMethodParameters"); }
  @Test void testMethodParametersAttr() { doTest("pkg/TestMethodParametersAttr"); }
  @Test void testCodeConstructs() { doTest("pkg/TestCodeConstructs"); }
  @Test void testConstants() { doTest("pkg/TestConstants"); }
  @Test void testEnum() { doTest("pkg/TestEnum"); }
  @Test void testDebugSymbols() { doTest("pkg/TestDebugSymbols"); }
  @Test void testInvalidMethodSignature() { doTest("InvalidMethodSignature"); }
  @Test void testAnonymousClassConstructor() { doTest("pkg/TestAnonymousClassConstructor"); }
  @Test void testInnerClassConstructor() { doTest("pkg/TestInnerClassConstructor"); }
  @Test void testInnerClassConstructor11() { doTest("v11/TestInnerClassConstructor"); }
  @Test void testTryCatchFinally() { doTest("pkg/TestTryCatchFinally"); }
  @Test void testAmbiguousCall() { doTest("pkg/TestAmbiguousCall"); }
  @Test void testAmbiguousCallWithDebugInfo() { doTest("pkg/TestAmbiguousCallWithDebugInfo"); }
  @Test void testSimpleBytecodeMapping() { doTest("pkg/TestClassSimpleBytecodeMapping"); }
  @Test void testSynchronizedMapping() { doTest("pkg/TestSynchronizedMapping"); }
  @Test void testAbstractMethods() { doTest("pkg/TestAbstractMethods"); }
  @Test void testLocalClass() { doTest("pkg/TestLocalClass"); }
  @Test void testAnonymousClass() { doTest("pkg/TestAnonymousClass"); }
  @Test void testThrowException() { doTest("pkg/TestThrowException"); }
  @Test void testInnerLocal() { doTest("pkg/TestInnerLocal"); }
  @Test void testInnerSignature() { doTest("pkg/TestInnerSignature"); }
  @Test void testAnonymousSignature() { doTest("pkg/TestAnonymousSignature"); }
  @Test void testLocalsSignature() { doTest("pkg/TestLocalsSignature"); }
  @Test void testParameterizedTypes() { doTest("pkg/TestParameterizedTypes"); }
  @Test void testShadowing() { doTest("pkg/TestShadowing", "pkg/Shadow", "ext/Shadow", "pkg/TestShadowingSuperClass"); }
  @Test void testStringConcat() { doTest("pkg/TestStringConcat"); }
  @Test void testJava9StringConcat() { doTest("java9/TestJava9StringConcat"); }
  @Test void testJava9ModuleInfo() { doTest("java9/module-info"); }
  @Test void testJava11StringConcat() { doTest("java11/TestJava11StringConcat"); }
  @Test void testMethodReferenceSameName() { doTest("pkg/TestMethodReferenceSameName"); }
  @Test void testMethodReferenceLetterClass() { doTest("pkg/TestMethodReferenceLetterClass"); }
  @Test void testConstructorReference() { doTest("pkg/TestConstructorReference"); }
  @Test void testMemberAnnotations() { doTest("pkg/TestMemberAnnotations"); }
  @Test void testMoreAnnotations() { doTest("pkg/MoreAnnotations"); }
  @Test void testTypeAnnotations() { doTest("pkg/TypeAnnotations"); }
  @Test void testStaticNameClash() { doTest("pkg/TestStaticNameClash"); }
  @Test void testExtendingSubclass() { doTest("pkg/TestExtendingSubclass"); }
  @Test void testSyntheticAccess() { doTest("pkg/TestSyntheticAccess"); }
  @Test void testIllegalVarName() { doTest("pkg/TestIllegalVarName"); }
  @Test void testIffSimplification() { doTest("pkg/TestIffSimplification"); }
  @Test void testKotlinConstructor() { doTest("pkg/TestKotlinConstructorKt"); }
  @Test void testAsserts() { doTest("pkg/TestAsserts"); }
  @Test void testLocalsNames() { doTest("pkg/TestLocalsNames"); }
  @Test void testAnonymousParamNames() { doTest("pkg/TestAnonymousParamNames"); }
  @Test void testAnonymousParams() { doTest("pkg/TestAnonymousParams"); }
  @Test void testAccessReplace() { doTest("pkg/TestAccessReplace"); }
  @Test void testStringLiterals() { doTest("pkg/TestStringLiterals"); }
  @Test void testPrimitives() { doTest("pkg/TestPrimitives"); }
  @Test void testClashName() { doTest("pkg/TestClashName", "pkg/SharedName1",
          "pkg/SharedName2", "pkg/SharedName3", "pkg/SharedName4", "pkg/NonSharedName",
          "pkg/TestClashNameParent", "ext/TestClashNameParent","pkg/TestClashNameIface", "ext/TestClashNameIface"); }
  @Test void testSwitchOnEnum() { doTest("pkg/TestSwitchOnEnum");}
  @Test void testVarArgCalls() { doTest("pkg/TestVarArgCalls"); }
  @Test void testLambdaParams() { doTest("pkg/TestLambdaParams"); }
  @Test void testInterfaceMethods() { doTest("pkg/TestInterfaceMethods"); }
  @Test void testConstType() { doTest("pkg/TestConstType"); }
  @Test void testPop2OneDoublePop2() { doTest("pkg/TestPop2OneDoublePop2"); }
  @Test void testPop2OneLongPop2() { doTest("pkg/TestPop2OneLongPop2"); }
  @Test void testPop2TwoIntPop2() { doTest("pkg/TestPop2TwoIntPop2"); }
  @Test void testPop2TwoIntTwoPop() { doTest("pkg/TestPop2TwoIntTwoPop"); }
  @Test void testSuperInner() { doTest("pkg/TestSuperInner", "pkg/TestSuperInnerBase"); }
  @Test void testMissingConstructorCallGood() { doTest("pkg/TestMissingConstructorCallGood"); }
  @Test void testMissingConstructorCallBad() { doTest("pkg/TestMissingConstructorCallBad"); }
  @Test void testEmptyBlocks() { doTest("pkg/TestEmptyBlocks"); }
  @Test void testInvertedFloatComparison() { doTest("pkg/TestInvertedFloatComparison"); }
  @Test void testPrivateEmptyConstructor() { doTest("pkg/TestPrivateEmptyConstructor"); }
  @Test void testSynchronizedUnprotected() { doTest("pkg/TestSynchronizedUnprotected"); }
  @Test void testInterfaceSuper() { doTest("pkg/TestInterfaceSuper"); }
  @Test void testFieldSingleAccess() { doTest("pkg/TestFieldSingleAccess"); }
  @Test void testPackageInfo() { doTest("pkg/package-info"); }

  // TODO: fix all below
  //@Test void testSwitchOnStrings() { doTest("pkg/TestSwitchOnStrings");}
  //@Test void testUnionType() { doTest("pkg/TestUnionType"); }
  //@Test void testInnerClassConstructor2() { doTest("pkg/TestInner2"); }
  //@Test void testInUse() { doTest("pkg/TestInUse"); }

  @Test void testGroovyClass() { doTest("pkg/TestGroovyClass"); }
  @Test void testGroovyTrait() { doTest("pkg/TestGroovyTrait"); }
  @Test void testPrivateClasses() { doTest("pkg/PrivateClasses"); }
  @Test void testSuspendLambda() { doTest("pkg/TestSuspendLambdaKt"); }
  @Test void testNamedSuspendFun2Kt() { doTest("pkg/TestNamedSuspendFun2Kt"); }
  @Test void testGenericArgs() { doTest("pkg/TestGenericArgs"); }
  @Test void testRecordEmpty() { doTest("records/TestRecordEmpty"); }
  @Test void testRecordSimple() { doTest("records/TestRecordSimple"); }
  @Test void testRecordVararg() { doTest("records/TestRecordVararg"); }
  @Test void testRecordGenericVararg() { doTest("records/TestRecordGenericVararg"); }
  @Test void testRecordAnno() { doTest("records/TestRecordAnno"); }

  private void doTest(String testFile, String... companionFiles) {
    ConsoleDecompiler decompiler = fixture.getDecompiler();

    File classFile = new File(fixture.getTestDataDir(), "/classes/" + testFile + ".class");
    assertTrue(classFile.isFile());
    for (File file : collectClasses(classFile)) {
      decompiler.addSource(file);
    }

    for (String companionFile : companionFiles) {
      File companionClassFile = new File(fixture.getTestDataDir(), "/classes/" + companionFile + ".class");
      assertTrue(companionClassFile.isFile());
      for (File file : collectClasses(companionClassFile)) {
        decompiler.addSource(file);
      }
    }

    decompiler.decompileContext();

    String testName = classFile.getName().substring(0, classFile.getName().length() - 6);
    File decompiledFile = new File(fixture.getTargetDir(), testName + ".java");
    assertTrue(decompiledFile.isFile());
    File referenceFile = new File(fixture.getTestDataDir(), "results/" + testName + ".dec");
    assertTrue(referenceFile.isFile());
    assertFilesEqual(referenceFile, decompiledFile);
  }

  private static List<File> collectClasses(File classFile) {
    List<File> files = new ArrayList<>();
    files.add(classFile);

    File parent = classFile.getParentFile();
    if (parent != null) {
      final String pattern = classFile.getName().replace(".class", "") + "\\$.+\\.class";
      File[] inner = parent.listFiles((dir, name) -> name.matches(pattern));
      if (inner != null) Collections.addAll(files, inner);
    }

    return files;
  }
}
