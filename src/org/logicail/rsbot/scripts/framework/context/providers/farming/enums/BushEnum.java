package org.logicail.rsbot.scripts.framework.context.providers.farming.enums;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject.Bush;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IFarmingObjectAccessor;
import org.powerbot.script.Identifiable;

import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.BUSH;
import static org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming.pretty;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 11:23
 */
public enum BushEnum implements Identifiable, IFarmingObjectAccessor<Bush> {
	CHAMPIONS_GUILD(BUSH[0]),
	RIMMINGTON(BUSH[1]),
	ETCETERIA(BUSH[2]),
	ARDOUGNE(BUSH[3]);
	private final int id;

	private volatile Bush instance = null;

	private final String pretty;

	BushEnum(int id) {
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

	public Bush object(IClientContext ctx) {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new Bush(ctx, this);
				}
			}
		}
		return instance;
	}
}

