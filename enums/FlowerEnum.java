package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Flower;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.FLOWER;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/04/2014
 * Time: 00:04
 */
public enum FlowerEnum implements Identifiable, IFarmingObjectAccessor<Flower> {
	FALADOR(FLOWER[0]),
	CATHERBY(FLOWER[1]),
	ARDOUGNE(FLOWER[2]),
	PORT_PHASMATYS(FLOWER[3]);
	private final int id;

	private  volatile Flower instance = null;

	private final String pretty;

	FlowerEnum(int id) {
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

	public Flower object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Flower(ctx, this);
				}
			}
		}
		return instance;
	}
}
