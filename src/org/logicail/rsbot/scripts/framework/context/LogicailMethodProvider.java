package org.logicail.rsbot.scripts.framework.context;

import org.powerbot.script.methods.MethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:52
 */
public class LogicailMethodProvider extends MethodProvider {
	protected final LogicailMethodContext ctx;

	public LogicailMethodProvider(LogicailMethodContext context) {
		super(context);
		this.ctx = context;
	}
}
