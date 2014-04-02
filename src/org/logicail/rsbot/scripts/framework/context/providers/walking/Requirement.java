package org.logicail.rsbot.scripts.framework.context.providers.walking;

import org.powerbot.script.rt6.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/02/14
 * Time: 18:13
 */
public interface Requirement {
	public boolean valid(ClientContext ctx);
}
