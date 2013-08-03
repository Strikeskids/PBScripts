package org.logicail.api.filters;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.wrappers.Actor;
import org.powerbot.script.wrappers.Identifiable;
import org.powerbot.script.wrappers.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 28/07/13
 * Time: 11:15
 */
public class CombatFilter<T extends Actor & Identifiable> extends LogicailMethodProvider implements Filter<T> {

	public CombatFilter(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public boolean accept(T t) {
		final Player local = ctx.players.local();
		final Actor interacting = t.getInteracting();
		return interacting == null || !t.isInCombat() || (interacting.equals(local) && t.getHealthPercent() > 0);
	}
}
