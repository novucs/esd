package net.novucs.esd.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import net.novucs.esd.orm.ParsedColumn;
import net.novucs.esd.orm.ParsedModel;

public final class ReflectUtil {

  private ReflectUtil() {
    throw new IllegalStateException();
  }

  public static <M> M constructModel(Class<M> modelClass, List<Object> modelAttributes) {
    try {
      M model = modelClass.getConstructor().newInstance();
      Iterator<ParsedColumn> columns = ParsedModel.of(modelClass).getColumns().values().iterator();
      Iterator<Object> valuesIterator = modelAttributes.iterator();

      while (columns.hasNext() && valuesIterator.hasNext()) {
        Object value = valuesIterator.next();
        ParsedColumn column = columns.next();

        Field field = modelClass.getDeclaredField(column.getName());
        field.setAccessible(true);
        field.set(model, value);
        field.setAccessible(false);
      }

      return model;
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Model classes must have an empty public constructor. "
          + modelClass.getSimpleName() + " violates this requirement.", e);
    }
  }

  public static <M> void setValue(M model, ParsedColumn column, Object value) {
    try {
      Field field = model.getClass().getDeclaredField(column.getName());
      field.setAccessible(true);
      field.set(model, value);
      field.setAccessible(false);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  public static <T, M> T getValue(M model, ParsedColumn column) {
    try {
      Field field = model.getClass().getDeclaredField(column.getName());
      field.setAccessible(true);
      Object value = field.get(model);
      field.setAccessible(false);
      //noinspection unchecked
      return (T) value;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
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
