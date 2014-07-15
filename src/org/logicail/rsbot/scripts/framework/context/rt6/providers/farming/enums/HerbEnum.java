package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject.Herb;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.IFarming.HERB;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 17:32
 */
public enum HerbEnum implements Identifiable, IFarmingObjectAccessor<Herb> {
	FALADOR(HERB[0]),
	CATHERBY(HERB[1]),
	ARDOUGNE(HERB[2]),
	PORT_PHASMATYS(HERB[3]),
	TROLLHEIM(HERB[4]);
	private final int id;

	private volatile Herb instance = null;

	private final String pretty;

	HerbEnum(int id) {
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

	public Herb object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Herb(ctx, this);
				}
			}
		}
		return instance;
	}
}
