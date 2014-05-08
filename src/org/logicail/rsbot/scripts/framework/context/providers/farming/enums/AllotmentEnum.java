package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Allotment;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.ALLOTMENT;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:54
 */
public enum AllotmentEnum implements Identifiable, IFarmingObjectAccessor<Allotment> {
	FALADOR_N(ALLOTMENT[0]),
	FALADOR_S(ALLOTMENT[1]),
	CATHERBY_N(ALLOTMENT[2]),
	CATHERBY_S(ALLOTMENT[3]),
	ARDOUGNE_N(ALLOTMENT[4]),
	ARDOUGNE_S(ALLOTMENT[5]),
	PORT_PHASMATYS_N(ALLOTMENT[6]),
	PORT_PHASMATYS_S(ALLOTMENT[7]);
	private final int id;

	private volatile Allotment instance = null;

	private final String pretty;

	AllotmentEnum(int id) {
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

	public Allotment object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Allotment(ctx, this);
				}
			}
		}
		return instance;
	}
}
