package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject.Hops;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.IFarming.HOPS;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 18:14
 */
public enum HopsEnum implements Identifiable, IFarmingObjectAccessor<Hops> {
	YANILLE(HOPS[0]),
	ENTRANA(HOPS[1]),
	LUMBRIDGE(HOPS[2]),
	SEERS_VILLAGE(HOPS[3]);
	private final int id;

	private volatile Hops instance = null;

	private final String pretty;

	HopsEnum(int id) {
		this.id = id;
		pretty = IFarming.pretty(name());
	}

	@Override
	public String toString() {
		return pretty;
	}

	@Override
	public int id() {
		return id;
	}

	public Hops object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Hops(ctx, this);
				}
			}
		}
		return instance;
	}
}