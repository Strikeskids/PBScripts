package org.logicail.rsbot.scripts.framework.context.providers.walking;

import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/02/14
 * Time: 18:13
 */
public interface Requirement {
	public boolean isValid(MethodContext ctx);
}
