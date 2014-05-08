package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Compost;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.COMPOST;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:47
 */
public enum CompostEnum implements Identifiable, IFarmingObjectAccessor<Compost> {
	FALADOR(COMPOST[0]),
	CATHERBY(COMPOST[1]),
	PORT_PHASMATYS(COMPOST[2]),
	ARDOUGNE(COMPOST[3]),
	HERBLORE_HABITAT(COMPOST[4]),
	TAVERLEY(COMPOST[5]);
	private final int id;

	private volatile Compost instance = null;

	private final String pretty;

	CompostEnum(int id) {
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

	public Compost object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Compost(ctx, this);
				}
			}
		}
		return instance;
	}
}
