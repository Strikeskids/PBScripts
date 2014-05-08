package org.logicail.rsbot.scripts.framework.context.providers.farming;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Identifiable;
import org.powerbot.script.rt6.IdQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 18:06
 */
public abstract class FarmingQuery<K extends Identifiable> extends IdQuery<K> {
	protected final IClientContext ctx;

	public FarmingQuery(IClientContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}
}
