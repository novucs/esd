package net.novucs.esd.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
  boolean primary() default false;
  Class<?> foreign() default void.class;
  boolean nullable() default false;
}
