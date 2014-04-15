package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Calquat;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.CALQUAT;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 22:47
 */
public enum CalquatEnum implements Identifiable, IFarmingObjectAccessor<Calquat> {
	TAI_BWO_WANNAI(CALQUAT);
	private final int id;

	private Calquat instance = null;

	private final String pretty;

	CalquatEnum(int id) {
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

	public Calquat object(IClientContext ctx) {
		return instance == null ? instance = new Calquat(ctx, this) : instance;
	}
}
