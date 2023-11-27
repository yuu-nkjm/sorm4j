package repackage.net.sf.persist.tests.engine.framework;

import java.util.ArrayList;
import java.util.List;

/** Holds all data necessary to build a bean during runtime and metadata to test it */
public class BeanMap {

  private final String className;
  private final List<FieldMap> fields;

  public BeanMap(String className) {
    this.className = className;
    this.fields = new ArrayList<>();
  }

  public String getClassName() {
    return className;
  }

  public List<FieldMap> getFields() {
    return fields;
  }

  public BeanMap addField(FieldMap fieldMap) {
    fields.add(fieldMap);
    return this;
  }

  public FieldMap getField(String fieldName) {
    for (FieldMap fieldMap : fields) {
      if (fieldMap.getFieldName().equals(fieldName)) return fieldMap;
    }
    return null;
  }
}
