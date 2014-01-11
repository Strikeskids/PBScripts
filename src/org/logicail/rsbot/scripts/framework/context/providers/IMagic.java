package org.logicail.rsbot.scripts.framework.context.providers;

import com.sk.methods.action.structure.Spell;
import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
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
			if (!spell.getWindow().isOpen(ctx)) {
				spell.getWindow().open(ctx);
				sleep(100, 300);
			}
			final Component component = spell.getComponent(ctx);
			return component.isValid() && component.isVisible() && component.interact("Cast");
		}
		return false;
	}
}
