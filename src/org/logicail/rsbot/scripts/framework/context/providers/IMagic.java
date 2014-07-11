package org.logicail.rsbot.scripts.framework.context.providers;

import com.sk.oldcombatapi.methods.action.structure.Spell;
import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.rt6.Action;
import org.powerbot.script.rt6.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 17:29
 */
public class IMagic extends IClientAccessor {
	public IMagic(IClientContext context) {
		super(context);
	}

	public boolean cast(Spell spell) {
		if (spell.getSpellbook().isOpen(ctx)) {
			final Action action = ctx.combatBar.select().id(spell.id()).poll();
			if (action.valid() && ctx.combatBar.expanded(true) && action.component().interact("Cast")) {
				return true;
			}

			if (spell.getWindow().open(ctx)) {
				final Component component = spell.getComponent(ctx);
				return component.valid() && component.visible() && component.interact("Cast");
			}
		}
		return false;
	}
}
