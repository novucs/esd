package net.novucs.esd.test.model;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The type Test each model.
 *
 * @param <M> the type parameter
 */
@RunWith(Parameterized.class)
public class TestEachModel<M> {

  private static final transient Password DUMMY_PASSWORD = Password.fromPlaintext("password");
  private static final transient ZonedDateTime DUMMY_DATE_TIME = ZonedDateTime.now();
  private final Class<M> modelClass;

  /**
   * Instantiates a new Test each model.
   *
   * @param modelClass the model class
   */
  public TestEachModel(Class<M> modelClass) {
    this.modelClass = modelClass;
  }

  /**
   * Data collection.
   *
   * @return the collection
   */
  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> data = new ArrayList<>();
    for (Class<?> modelClass : DatabaseLifecycle.MODEL_CLASSES) {
      data.add(new Object[]{modelClass});
    }
    return data;
  }

  private Object getTestData(Class<?> type) {
    if (type == String.class) {
      return "string";
    } else if (type == Integer.class) {
      return 1;
    } else if (type == Boolean.class) {
      return false;
    } else if (type == Password.class) {
      return DUMMY_PASSWORD;
    } else if (type == BigDecimal.class) {
      return new BigDecimal("100.5");
    } else if (type == ZonedDateTime.class) {
      return DUMMY_DATE_TIME;
    }
    throw new IllegalArgumentException("No test data for type: " + type.getName());
  }

  /**
   * Test equals.
   *
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testEquals() throws ReflectiveOperationException {
    M model1 = createModelWithSetters();
    M model2 = createModelWithConstructor();
    assertEquals("Similar models should be equal", model1, model2);
  }

  /**
   * Test hash code.
   *
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testHashCode() throws ReflectiveOperationException {
    M model1 = createModelWithSetters();
    M model2 = createModelWithConstructor();
    assertEquals("Similar models hash code should be equal",
        model1.hashCode(), model2.hashCode());
  }

  private M createModelWithConstructor() throws ReflectiveOperationException {
    Constructor constructor = null;
    for (Constructor c : modelClass.getConstructors()) {
      if (c.getParameterCount() > 0) {
        constructor = c;
      }
    }

    List<Object> parameters = new ArrayList<>();

    assert constructor != null;
    for (Parameter parameter : constructor.getParameters()) {
      parameters.add(getTestData(parameter.getType()));
    }

    @SuppressWarnings("unchecked")
    M model2 = (M) constructor.newInstance(parameters.toArray());
    ReflectUtil.setFieldValue(model2, "id", getTestData(Integer.class));
    return model2;
  }

  private M createModelWithSetters() throws ReflectiveOperationException {
    M model = modelClass.getConstructor().newInstance();

    for (Method method : modelClass.getDeclaredMethods()) {
      if (!(method.getName().startsWith("set") && method.getParameterCount() == 1)) {
        continue;
      }

      Class<?> parameter = method.getParameters()[0].getType();
      Object testData = getTestData(parameter);
      method.invoke(model, testData);
    }
    return model;
  }
}
