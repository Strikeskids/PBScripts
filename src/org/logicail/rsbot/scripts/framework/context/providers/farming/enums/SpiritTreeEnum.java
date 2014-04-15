package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.SpiritTree;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.SPIRIT_TREE;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 23:36
 */
public enum SpiritTreeEnum implements Identifiable, IFarmingObjectAccessor<SpiritTree> {
	PORT_SARIM(SPIRIT_TREE[0]),
	ETCETERIA(SPIRIT_TREE[1]),
	BRIMHAVEN(SPIRIT_TREE[2]);
	private final int id;

	private SpiritTree instance = null;

	private final String pretty;

	SpiritTreeEnum(int id) {
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

	public SpiritTree object(IClientContext ctx) {
		return instance == null ? instance = new SpiritTree(ctx, this) : instance;
	}
}
