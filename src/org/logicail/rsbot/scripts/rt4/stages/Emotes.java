package org.logicail.rsbot.scripts.rt4.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 17:53
 */
public class Emotes extends GraphScript.Action<IClientContext> {
	public Emotes(IClientContext ctx) {
		super(ctx);
	}

	@Override
	public String toString() {
		return "Emotes";
	}

	@Override
	public void run() {
		Condition.sleep(500);
		ctx.inventory.deselect();
		if (ctx.game.tab() == Game.Tab.EMOTES || ctx.game.tab(Game.Tab.EMOTES)) {
			final Component emote = ctx.widgets.widget(464).component(Random.nextInt(38, 58));
			if (emote.valid() && emote.click()) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().animation() != -1;
					}
				}, 100, 10)) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.players.local().animation() == -1 && ctx.chat.visible("It's only a short distance to the next guide");
						}
					}, 200, 30);
				}
			}
		}
	}

	@Override
	public boolean valid() {
		return ctx.chat.visible("Now, how about showing some feelings?", "For those situations where words don't quite");
	}
}
