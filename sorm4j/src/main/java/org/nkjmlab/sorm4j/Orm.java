package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.CommandFunction;

/**
 * ORM functions with an instant connection. When executing ORM function, this object gets a
 * connection and executes the function, after that closes the connection immediately.
 *
 * @author nkjm
 *
 */
@Experimental
public interface Orm
    extends OrmFunction, OrmFunctionWithUniqueMapping, OrmLazyReader, SqlExecutor, CommandFunction {

}
