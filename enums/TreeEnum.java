package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.patches.Tree;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.TREE;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 19:21
 */
public enum TreeEnum implements Identifiable {
	TAVERLEY(TREE[0]),
	FALADOR(TREE[1]),
	VARROCK(TREE[2]),
	LUMBRIDGE(TREE[3]);

	private final int id;

	private Tree instance = null;

	private final String pretty;

	TreeEnum(int id) {
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

	public Tree tree(IClientContext ctx) {
		return instance == null ? instance = new Tree(ctx, this) : instance;
	}
}
