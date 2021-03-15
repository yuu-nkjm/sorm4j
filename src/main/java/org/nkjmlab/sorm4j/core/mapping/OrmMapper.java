package org.nkjmlab.sorm4j.core.mapping;

import org.nkjmlab.sorm4j.OrmMapReader;
import org.nkjmlab.sorm4j.OrmReader;
import org.nkjmlab.sorm4j.OrmUpdater;
import org.nkjmlab.sorm4j.SqlExecutor;

interface OrmMapper extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor {



}
