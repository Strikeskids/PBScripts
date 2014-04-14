package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.patches.Compost;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.COMPOST;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:47
 */
public enum CompostEnum implements Identifiable {
	FALADOR(COMPOST[0]),
	CATHERBY(COMPOST[1]),
	PORT_PHASMATYS(COMPOST[3]),
	ARDOUGNE(COMPOST[4]);
	private final int id;

	private Compost instance = null;

	CompostEnum(int id) {
		this.id = id;
	}

	public Compost compost(IClientContext ctx) {
		return instance == null ? instance = new Compost(ctx, this) : instance;
	}

	@Override
	public int id() {
		return id;
	}
}
