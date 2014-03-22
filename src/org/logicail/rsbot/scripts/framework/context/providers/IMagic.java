package org.logicail.rsbot.scripts.framework.context.providers;

import com.sk.methods.action.structure.Spell;
import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 17:29
 */
public class IMagic extends IMethodProvider {
	public IMagic(IMethodContext context) {
		super(context);
	}

	public boolean cast(Spell spell) {
		if (spell.getSpellbook().isOpen(ctx)) {
			final Action action = ctx.combatBar.select().id(spell.getId()).poll();
			if (action.isValid() && ctx.combatBar.setExpanded(true) && action.getComponent().interact("Cast")) {
				return true;
			}

			if (spell.getWindow().open(ctx)) {
				final Component component = spell.getComponent(ctx);
				return component.isValid() && component.isVisible() && component.interact("Cast");
			}
		}
		return false;
	}
}
