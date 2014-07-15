package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject.FruitTree;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.IFarming.FRUIT_TREE;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 21:01
 */
public enum FruitTreeEnum implements Identifiable, IFarmingObjectAccessor<FruitTree> {
	TREE_GNOME_STRONGHOLD(FRUIT_TREE[0]),
	TREE_GNOME_VILLAGE(FRUIT_TREE[1]),
	BRIMHAVEN(FRUIT_TREE[2]),
	CATHERBY(FRUIT_TREE[3]),
	LLETYA(FRUIT_TREE[4]),
	HERBLORE_HABITAT(FRUIT_TREE[5]);

	private final int id;

	private volatile FruitTree instance = null;

	private final String pretty;

	FruitTreeEnum(int id) {
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

	public FruitTree object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new FruitTree(ctx, this);
				}
			}
		}
		return instance;
	}
}
