package org.jetbrains.java.decompiler;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Field;
import java.util.List;

public class DecTestFixtureExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    if (context.getTestInstance().isPresent()) {
      Object instance = context.getTestInstance().get();

      List<Field> fields = AnnotationSupport.findAnnotatedFields(instance.getClass(), DecTestFixture.class);

      for (Field field : fields) {
        field.setAccessible(true);

        DecTestFixture params = field.getAnnotation(DecTestFixture.class);

        DecompilerTestFixture fixture = new DecompilerTestFixture();
        fixture.setUp(params.value());
        field.set(instance, fixture);
      }
    }
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    if (context.getTestInstance().isPresent()) {
      Object instance = context.getTestInstance().get();

      List<Field> fields = AnnotationSupport.findAnnotatedFields(instance.getClass(), DecTestFixture.class);

      for (Field field : fields) {
        field.setAccessible(true);

        DecompilerTestFixture fixture = (DecompilerTestFixture) field.get(instance);
        fixture.tearDown();

        field.set(instance, null);
      }
    }
  }
}
