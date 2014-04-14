package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.patches.FruitTree;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.FRUIT_TREE;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 21:01
 */
public enum FruitTreeEnum implements Identifiable {
	TREE_GNOME_STRONGHOLD(FRUIT_TREE[0]),
	TREE_GNOME_VILLAGE(FRUIT_TREE[1]),
	BRIMHAVEN(FRUIT_TREE[2]),
	CATHERBY(FRUIT_TREE[3]),
	LLETYA(FRUIT_TREE[4]),
	HERBLORE_HABITAT(FRUIT_TREE[5]);

	private final int id;

	private FruitTree instance = null;

	FruitTreeEnum(int id) {
		this.id = id;
	}

	@Override
	public int id() {
		return id;
	}

	public FruitTree tree(IClientContext ctx) {
		return instance == null ? instance = new FruitTree(ctx, this) : instance;
	}
}
