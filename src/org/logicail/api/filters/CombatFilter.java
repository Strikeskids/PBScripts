package org.logicail.api.filters;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.lang.Identifiable;
import org.powerbot.script.wrappers.Actor;
import org.powerbot.script.wrappers.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 28/07/13
 * Time: 11:15
 */
public class CombatFilter<T extends Actor & Identifiable> extends LogicailMethodProvider implements Filter<T> {

	private final int[] ids;

	public CombatFilter(LogicailMethodContext context, int... ids) {
		super(context);
		this.ids = ids;
	}

	@Override
	public boolean accept(T t) {
		final Player local = ctx.players.local();
		for (int id : ids) {
			if (t.getId() == id) {
				final Actor interacting = t.getInteracting();
				if (interacting == null || !t.isInCombat() || (interacting.equals(local) && t.getHealthPercent() > 0)) {
					return true;
				}
			}
		}
		return false;
	}
}
