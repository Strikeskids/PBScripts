package org.logicail.rsbot.scripts.rt4.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/07/2014
 * Time: 23:52
 */
public class TabOpener extends GraphScript.Action<IClientContext> {

	@Override
	public String toString() {
		return "Tab Opener";
	}


	public TabOpener(IClientContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		final int i = tab();
		switch (i) {
			case 1:
				ctx.game.tab(Game.Tab.ATTACK);
				break;
			case 2:
				ctx.game.tab(Game.Tab.STATS);
				break;
			case 3:
				ctx.game.tab(Game.Tab.QUESTS);
				break;
			case 4:
				ctx.game.tab(Game.Tab.INVENTORY);
				break;
			case 5:
				ctx.game.tab(Game.Tab.EQUIPMENT);
				break;
			case 6:
				ctx.game.tab(Game.Tab.PRAYER);
				break;
			case 7:
				ctx.game.tab(Game.Tab.MAGIC);
				break;
			case 9:
				ctx.game.tab(Game.Tab.FRIENDS_LIST);
				break;
			case 10:
				final Component component = findTextureId(905);
				if (component != null && component.click("Ignore List")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() {
							return ctx.game.tab() == Game.Tab.IGNORED_LIST;
						}
					}, 50, 10);
				} else {
					ctx.game.tab(Game.Tab.IGNORED_LIST);
				}
				break;
			case 12:
				ctx.game.tab(Game.Tab.OPTIONS);
				break;
			case 13:
				ctx.game.tab(Game.Tab.EMOTES);
				break;
			case 14:
				ctx.game.tab(Game.Tab.MUSIC);
				break;
			default:
				log.severe("I don't know which tab to open [" + i + "]");
				ctx.controller.stop();
				return;
		}
		Condition.sleep(200);
	}

	private Component findTextureId(final int texture) {
		for (Component component : this.ctx.widgets.widget(548).components()) {
			if (component.textureId() == texture) {
				return component;
			}
		}

		return null;
	}

	@Override
	public boolean valid() {
		return tab() > 0;
	}

	private int tab() {
		return ctx.varpbits.varpbit(1021) & 0xf;
	}
}
