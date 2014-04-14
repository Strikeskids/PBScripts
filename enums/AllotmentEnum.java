package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.patches.Allotment;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.ALLOTMENT;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:54
 */
public enum AllotmentEnum implements Identifiable {
	FALADOR_N(ALLOTMENT[0]),
	FALADOR_S(ALLOTMENT[1]);
	private final int id;

	private Allotment instance = null;

	AllotmentEnum(int id) {
		this.id = id;
	}

	public Allotment allotment(IClientContext ctx) {
		return instance == null ? instance = new Allotment(ctx, this) : instance;
	}

	@Override
	public int id() {
		return id;
	}
}
