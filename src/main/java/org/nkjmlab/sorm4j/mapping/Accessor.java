package org.nkjmlab.sorm4j.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.util.StringUtils;

final class Accessor {

  private final GetterAccessor getter;
  private final SetterAccessor setter;
  private final Column column;


  Accessor(Column column, Field field, Method getter, Method setter) {
    this.column = column;
    this.getter = getter != null ? new GetterMethod(getter) : new FieldGetter(field);
    this.setter = setter != null ? new SetterMethod(setter) : new FieldSetter(field);
  }

  public SetterAccessor getSetter() {
    return setter;
  }


  public Object get(Object object) {
    return getter.get(object);
  }

  public void set(Object object, Object value) {
    setter.set(object, value);
  }

  @Override
  public String toString() {
    return "Accessor [getterAccessor=" + getter + ", setterAccessor=" + setter + ", column="
        + column + "]";
  }


  public String getFormattedString() {
    return "getter=[" + getter + "], setter=[" + setter + "]";
  }


  private interface GetterAccessor {
    Object get(Object object);
  }

  private class GetterMethod implements GetterAccessor {

    private final Method getter;

    public GetterMethod(Method getter) {
      this.getter = getter;
    }

    @Override
    public Object get(Object object) {
      try {
        return getter.invoke(object, new Object[] {});
      } catch (Exception e) {
        throw new OrmException("Could not access getter for column [" + column + "]", e);
      }
    }

    @Override
    public String toString() {
      return getter.getReturnType().getSimpleName() + " " + getter.getName() + "()";
    }


  }


  private class FieldGetter implements GetterAccessor {

    private final Field field;

    public FieldGetter(Field field) {
      this.field = field;
    }

    @Override
    public Object get(Object object) {
      try {
        return field.get(object);
      } catch (Exception e) {
        throw new OrmException(
            StringUtils.format("Could not access field for column [{}] with [{}]", column, this),
            e);
      }
    }

    @Override
    public String toString() {
      return "field " + field.getType().getSimpleName() + " " + field.getName();
    }

  }

  private interface SetterAccessor {
    void set(Object object, Object value);

    Class<?> getParameterType();
  }

  private class SetterMethod implements SetterAccessor {

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
    public void set(Object object, Object value) {
      try {
        setter.invoke(object, new Object[] {value});
      } catch (Exception e) {
        throw new OrmException(StringUtils.format(
            "Error setting value [{} ({})]" + " from column [{}] using setter [{}]: {}", value,
            value.getClass().getName(), column, this.toString(), e.getMessage(), e));
      }
    }

    @Override
    public String toString() {
      return setter.getName() + "(" + setter.getParameterTypes()[0].getSimpleName() + ")";
    }

  }


  private class FieldSetter implements SetterAccessor {

    private Field field;

    public FieldSetter(Field field) {
      this.field = field;
    }

    @Override
    public Class<?> getParameterType() {
      Class<?> type = field.getType();
      return type;
    }

    @Override
    public void set(Object object, Object value) {
      try {
        field.set(object, value);
      } catch (Exception e) {
        throw new OrmException(StringUtils.format(
            "Error setting value [{}]" + " of type [{}]" + " from column [{}] using field [{}]: {}",
            value, value.getClass().getName(), column, field, e.getMessage(), e));
      }
    }

    @Override
    public String toString() {
      return "field " + field.getType().getSimpleName() + " " + field.getName();
    }

  }

  public Class<?> getSetterParameterType() {
    return setter.getParameterType();
  }



}
