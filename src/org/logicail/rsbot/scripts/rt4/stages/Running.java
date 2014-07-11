package org.logicail.rsbot.scripts.rt4.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.ActionManifest;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Game;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 18:00
 */
@ActionManifest(name = "Running")
public class Running extends GraphScript.Action<IClientContext> {
	private static final String IT_S_ONLY_A_SHORT_DISTANCE_TO_THE_NEXT_GUIDE = "It's only a short distance to the next guide.";
	private static final String CLICK_ON_THE_RUN_BUTTON_NOW = "click on the run button now.";

	public Running(IClientContext ctx) {
		super(ctx);

		chain.add(new GraphScript.Action<IClientContext>(ctx) {
			@Override
			public void run() {
				if (ctx.chat.visible(IT_S_ONLY_A_SHORT_DISTANCE_TO_THE_NEXT_GUIDE)) {
					ctx.game.tab(Game.Tab.OPTIONS);
					Condition.sleep(500);
				}
			}
		});

		chain.add(new GraphScript.Action<IClientContext>(ctx) {
			@Override
			public void run() {
				if (ctx.chat.visible(CLICK_ON_THE_RUN_BUTTON_NOW)) {
					ctx.movement.running(true);
					Condition.sleep(500);
				}
			}
		});
	}

	@Override
	public void run() {
	}
}
