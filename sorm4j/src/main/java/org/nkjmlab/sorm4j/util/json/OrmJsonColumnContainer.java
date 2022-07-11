package org.nkjmlab.sorm4j.util.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Experimental
public @interface OrmJsonColumnContainer {

}
