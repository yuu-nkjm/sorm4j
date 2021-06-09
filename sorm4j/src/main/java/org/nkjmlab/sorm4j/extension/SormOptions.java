package org.nkjmlab.sorm4j.extension;

import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Represents options of Sorm.
 *
 * @author nkjm
 *
 */
@Experimental
public interface SormOptions {

  /**
   * Gets value by key.
   *
   * @param key
   * @return
   */
  Object get(String key);

  /**
   * Gets value by key or default value.
   *
   * @param <T>
   * @param key
   * @param defaultValue
   * @return
   */
  <T> T getOrDefault(String key, T defaultValue);

}
