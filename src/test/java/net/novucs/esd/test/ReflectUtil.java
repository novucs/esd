package net.novucs.esd.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ReflectUtil {

  private ReflectUtil() {
  }

  private static void setFieldValueInner(Class clazz, Object object, String fieldName, Object value)
      throws ReflectiveOperationException {
    // Remove fields final modifier
    Field field = clazz.getDeclaredField(fieldName);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    final boolean wasFinal = (field.getModifiers() & Modifier.FINAL) > 0;
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

    // Make field public
    boolean wasAccessible = field.isAccessible();
    field.setAccessible(true);

    // Update field
    field.set(object, value);

    // Make field private
    field.setAccessible(wasAccessible);

    // Add back field modifier
    if (wasFinal) {
      modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
    }
    modifiersField.setAccessible(false);
  }

  public static void setFieldValue(Object object, String fieldName, Object value)
      throws ReflectiveOperationException {
    setFieldValueInner(object.getClass(), object, fieldName, value);
  }

  public static void setStaticFieldValue(Class clazz, String fieldName, Object value)
      throws ReflectiveOperationException {
    setFieldValueInner(clazz, null, fieldName, value);
  }

  public static void executePrivateStaticMethod(Class clazz, String methodName, Class[] classes,
      Object... params)
      throws Throwable {
    Method method = clazz.getDeclaredMethod(methodName, classes);
    boolean wasAccessible = method.isAccessible();
    method.setAccessible(true);

    try {
      method.invoke(null, params);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    } finally {
      method.setAccessible(wasAccessible);
    }
  }
}
