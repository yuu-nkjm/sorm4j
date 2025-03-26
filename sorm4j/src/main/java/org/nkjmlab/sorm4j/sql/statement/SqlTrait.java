package org.nkjmlab.sorm4j.sql.statement;

/** Base trait that combines all SQL-related traits. */
public interface SqlTrait extends SqlSelectTrait, SqlConditionTrait, SqlStringTrait {}
