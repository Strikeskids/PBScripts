package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Mushroom;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.MUSHROOM;
import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.pretty;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 12:23
 */
public enum MushroomEnum implements Identifiable, IFarmingObjectAccessor<Mushroom> {
	CANIFIS(MUSHROOM);
	private final int id;

	private volatile Mushroom instance = null;

	private final String pretty;

	MushroomEnum(int id) {
		this.id = id;
		pretty = pretty(name());
	}

	@Override
	public String toString() {
		return pretty;
	}

	@Override
	public int id() {
		return id;
	}

	public Mushroom object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Mushroom(ctx, this);
				}
			}
		}
		return instance;
	}
}