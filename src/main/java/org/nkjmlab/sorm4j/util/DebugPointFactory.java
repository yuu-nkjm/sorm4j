package org.nkjmlab.sorm4j.util;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class DebugPointFactory {

  private DebugPointFactory() {}

  public enum Name {
    MAPPING, READ, LOAD_OBJECT, EXECUTE_BATCH, EXECUTE_UPDATE;
  }

  private static final Map<Name, Boolean> modes = new EnumMap<>(Name.class);

  public static void setModes(Map<Name, Boolean> map) {
    modes.putAll(map);
  }

  public static void on() {
    Arrays.stream(Name.values()).forEach(name -> modes.put(name, true));
  }


  public static void off() {
    Arrays.stream(Name.values()).forEach(name -> modes.put(name, false));
  }

  public static Optional<DebugPoint> createDebugPoint(Name name) {
    Boolean f = modes.get(name);
    if (f == null || !f) {
      return Optional.empty();
    } else {
      DebugPoint dp = new DebugPoint(name.name());
      return Optional.of(dp);
    }
  }



}
