package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.rt6.MobileIdNameQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 18:06
 */
public abstract class FarmingQuery<K extends Identifiable & Locatable & Nameable> extends MobileIdNameQuery<K> {
	protected final IClientContext ctx;

	public FarmingQuery(IClientContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}
}
