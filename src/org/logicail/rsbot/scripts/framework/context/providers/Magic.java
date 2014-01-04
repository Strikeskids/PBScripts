package org.logicail.rsbot.scripts.framework.context.providers;

import com.sk.methods.action.structure.Spell;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.powerbot.script.wrappers.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 17:29
 */
public class Magic extends LogicailMethodProvider {
	public Magic(LogicailMethodContext context) {
		super(context);
	}

	public boolean cast(Spell spell) {
		if (spell.getSpellbook().isOpen(ctx) && spell.getWindow().open(ctx)) {
			if (!spell.getWindow().isOpen(ctx)) {
				if (spell.getWindow().open(ctx)) {
					sleep(50, 500);
				}
			}
			final Component component = spell.getComponent(ctx);
			return component.isValid() && component.isVisible() && component.interact("Cast");
		}
		return false;
	}
}
