package org.logicail.rsbot.scripts.framework.context.providers;

import com.sk.methods.action.structure.Spell;
import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;

import java.util.concurrent.Callable;

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
			if (ctx.combatBar.select().id(spell.getId()).poll().select()) {
				return true;
			}

			final Component component = spell.getComponent(ctx);

			if (!spell.getWindow().isOpen(ctx)) {
				spell.getWindow().open(ctx);
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return component.isValid() && component.isVisible();
					}
				}, 250, 6);
			}
			
			return component.isValid() && component.isVisible() && component.interact("Cast");
		}
		return false;
	}
}
