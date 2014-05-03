package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Herb;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.HERB;

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

	private Herb herb = null;

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
		return herb == null ? herb = new Herb(ctx, this) : herb;
	}
}
