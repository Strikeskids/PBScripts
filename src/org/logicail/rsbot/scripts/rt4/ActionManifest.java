package org.logicail.rsbot.scripts.rt4;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/07/2014
 * Time: 10:31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ActionManifest {
	java.lang.String name();
}
