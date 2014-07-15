package org.logicail.rsbot.scripts.framework.context.rt6;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:52
 */
public class IClientAccessor extends org.powerbot.script.rt6.ClientAccessor {
	protected final IClientContext ctx;

	public IClientAccessor(IClientContext context) {
		super(context);
		this.ctx = context;
	}
}
