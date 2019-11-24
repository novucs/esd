package net.novucs.esd.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Column.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

  /**
   * Primary boolean.
   *
   * @return the boolean
   */
  boolean primary() default false;

  /**
   * Foreign class.
   *
   * @return the class
   */
  Class<?> foreign() default void.class;

  /**
   * Nullable boolean.
   *
   * @return the boolean
   */
  boolean nullable() default false;

  /**
   * Adds the column to the provided named unique constraint.
   *
   * @return the unique constraint name
   */
  String unique() default "";

}
