package org.nkjmlab.sorm4j.util.table;

import java.util.stream.Stream;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.TableMappedOrm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;

@Experimental
public interface Table<T> extends TableMappedOrm<T> {

  /**
   * Gets Sorm objects
   *
   * @return
   */
  @Override
  Sorm getOrm();

  /**
   *
   * @param gen
   * @param strm
   */
  @Experimental
  default void acceptHandler(FunctionHandler<TypedOrmStreamGenerator<T>, Stream<T>> gen,
      ConsumerHandler<Stream<T>> strm) {
    getOrm().acceptHandler(conn -> gen.apply(new TypedOrmStreamGenerator<T>(getValueType(), conn)),
        strm);
  }

  /**
   *
   * @param <R>
   * @param gen
   * @param strm
   * @return
   */
  @Experimental
  default <R> R applyHandler(FunctionHandler<TypedOrmStreamGenerator<T>, Stream<T>> gen,
      FunctionHandler<Stream<T>, R> strm) {
    return getOrm().applyHandler(
        conn -> gen.apply(new TypedOrmStreamGenerator<T>(getValueType(), conn)), strm);
  }


  static <T> Table<T> create(Sorm sorm, Class<T> objectClass) {
    return new BasicTable<>(sorm, objectClass);
  }

}
