package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Belladonna;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.BELLADONNA;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 09:05
 */
public enum BelladonnaEnum implements Identifiable, IFarmingObjectAccessor<Belladonna> {
	DRAYNOR(BELLADONNA);
	private final int id;

	private volatile Belladonna instance = null;

	private final String pretty;

	BelladonnaEnum(int id) {
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

	public Belladonna object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Belladonna(ctx, this);
				}
			}
		}
		return instance;
	}
}