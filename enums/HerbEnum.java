package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.patches.Herb;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.HERB;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 17:32
 */
public enum HerbEnum implements Identifiable {
	FALADOR(HERB[0]),
	CATHERBY(HERB[1]),
	ARDOUGNE(HERB[2]),
	PORT_PHASMATYS(HERB[3]),
	TROLLHEIM(HERB[4]);
	private final int id;

	private Herb herb = null;

	HerbEnum(int id) {
		this.id = id;
	}

	public Herb herb(IClientContext ctx) {
		return herb == null ? herb = new Herb(ctx, this) : herb;
	}

	@Override
	public int id() {
		return id;
	}
}
