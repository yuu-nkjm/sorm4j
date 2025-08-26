package org.nkjmlab.sorm4j.internal.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nkjmlab.sorm4j.mapping.annotation.OrmIgnore;

public class RefrectionOrmComponentUtils {
  public static record OrmContainerComponent(
      int order, String name, Class<?> type, Annotation[] annotations) {}

  public static List<Field> getDeclaredFields(Class<?> valueType) {
    return getFieldsAux(valueType.getDeclaredFields());
  }

  public static List<Field> getFields(Class<?> valueType) {
    return getFieldsAux(valueType.getFields());
  }

  private static List<Field> getFieldsAux(Field[] fields) {
    return Arrays.stream(fields)
        .filter(
            f ->
                !java.lang.reflect.Modifier.isStatic(f.getModifiers())
                    && !f.getName().startsWith("this$")
                    && !f.isAnnotationPresent(OrmIgnore.class))
        .toList();
  }

  public static List<OrmContainerComponent> getOrmComponents(Class<?> valueType) {
    List<OrmContainerComponent> ret = new ArrayList<>();
    if (valueType.isRecord()) {
      List<RecordComponent> components = getRecordComponents(valueType);
      for (int i = 0; i < components.size(); i++) {
        RecordComponent component = components.get(i);
        ret.add(
            new OrmContainerComponent(
                i, component.getName(), component.getType(), component.getAnnotations()));
      }
    } else {
      List<Field> fields = getDeclaredFields(valueType);
      for (int i = 0; i < fields.size(); i++) {
        Field field = fields.get(i);
        ret.add(
            new OrmContainerComponent(i, field.getName(), field.getType(), field.getAnnotations()));
      }
    }
    return ret;
  }

  static List<RecordComponent> getRecordComponents(Class<?> recordClass) {
    return Arrays.stream(recordClass.getRecordComponents())
        .filter(c -> !c.isAnnotationPresent(OrmIgnore.class))
        .toList();
  }

  private RefrectionOrmComponentUtils() {}
}
