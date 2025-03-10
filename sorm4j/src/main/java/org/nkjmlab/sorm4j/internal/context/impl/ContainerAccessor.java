package org.nkjmlab.sorm4j.internal.context.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nkjmlab.sorm4j.common.exception.SormException;

/**
 * Field accessor mapping to the columnName.
 *
 * @author nkjm
 */
public final class ContainerAccessor {

  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};
  private static final EmptyGetter EMPTY_GETTER = new EmptyGetter();
  private static final EmptySetter EMPTY_SETTER = new EmptySetter();
  private final GetterAccessor getter;
  private final SetterAccessor setter;
  private final String columnName;

  public ContainerAccessor(String columnName, Field field, Method getter, Method setter) {
    this.columnName = columnName;
    this.getter =
        getter != null
            ? new GetterMethod(getter)
            : field != null ? new FieldGetter(field) : EMPTY_GETTER;
    this.setter =
        setter != null
            ? new SetterMethod(setter)
            : field != null ? new FieldSetter(field) : EMPTY_SETTER;
  }

  /**
   * Gets the value from the corresponding field by getter method/field access.
   *
   * @param object
   * @return
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   */
  public Object get(Object object)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return getter.get(object);
  }

  /**
   * Sets the given value to the corresponding field by setter method/field access.
   *
   * @param object
   * @param value
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   */
  public void set(Object object, Object value)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    setter.set(object, value);
  }

  /**
   * Gets parameter type of setter method/field access.
   *
   * @return
   */
  public final Class<?> getSetterParameterType() {
    return setter.getParameterType();
  }

  public final Class<?> getGetterReturnType() {
    return getter.getReturnType();
  }

  @Override
  public String toString() {
    return "Accessor [getterAccessor="
        + getter
        + ", setterAccessor="
        + setter
        + ", columnName="
        + columnName
        + "]";
  }

  public String getFormattedString() {
    return "getter=[" + getter + "], setter=[" + setter + "]";
  }

  private static interface GetterAccessor {
    Object get(Object object)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    Class<?> getReturnType();
  }

  private static final class GetterMethod implements GetterAccessor {

    private final Method getter;

    public GetterMethod(Method getter) {
      this.getter = getter;
    }

    @Override
    public Object get(Object object)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return getter.invoke(object, EMPTY_OBJECT_ARRAY);
    }

    @Override
    public String toString() {
      return getter.getReturnType().getSimpleName() + " " + getter.getName() + "()";
    }

    @Override
    public Class<?> getReturnType() {
      return getter.getReturnType();
    }
  }

  private static final class EmptyGetter implements GetterAccessor {

    @Override
    public Object get(Object object)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      throw new SormException("No valid getter for " + object);
    }

    @Override
    public String toString() {
      return getClass().getSimpleName();
    }

    @Override
    public Class<?> getReturnType() {
      return Void.class;
    }
  }

  private static final class FieldGetter implements GetterAccessor {

    private final Field field;

    public FieldGetter(Field field) {
      this.field = field;
    }

    @Override
    public Object get(Object object) throws IllegalArgumentException, IllegalAccessException {
      return field.get(object);
    }

    @Override
    public String toString() {
      return "field " + field.getType().getSimpleName() + " " + field.getName();
    }

    @Override
    public Class<?> getReturnType() {
      return field.getType();
    }
  }

  private static interface SetterAccessor {

    void set(Object object, Object value)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    Class<?> getParameterType();
  }

  private static final class EmptySetter implements SetterAccessor {

    @Override
    public void set(Object object, Object value)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      throw new SormException("No valid setter for " + object);
    }

    @Override
    public Class<?> getParameterType() {
      throw new SormException("No valid setter");
    }

    @Override
    public String toString() {
      return getClass().getSimpleName();
    }
  }

  private static final class SetterMethod implements SetterAccessor {

    private final Method setter;

    public SetterMethod(Method setter) {
      this.setter = setter;
    }

    @Override
    public Class<?> getParameterType() {
      Class<?> type = setter.getParameterTypes()[0];
      return type;
    }

    @Override
    public void set(Object object, Object value)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      setter.invoke(object, value);
    }

    @Override
    public String toString() {
      return setter.getName() + "(" + setter.getParameterTypes()[0].getSimpleName() + ")";
    }
  }

  private static final class FieldSetter implements SetterAccessor {

    private final Field field;

    public FieldSetter(Field field) {
      this.field = field;
    }

    @Override
    public Class<?> getParameterType() {
      Class<?> type = field.getType();
      return type;
    }

    @Override
    public void set(Object object, Object value)
        throws IllegalArgumentException, IllegalAccessException {
      field.set(object, value);
    }

    @Override
    public String toString() {
      return "field " + field.getType().getSimpleName() + " " + field.getName();
    }
  }
}
