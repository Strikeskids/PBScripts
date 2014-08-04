package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Game;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/07/2014
 * Time: 23:52
 */
public class TabOpener extends GraphScript.Action<RT4ClientContext> {

	@Override
	public String toString() {
		return "Tab Opener";
	}


	public TabOpener(RT4ClientContext ctx) {
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
				ctx.game.tab(Game.Tab.IGNORED_LIST);
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
		Condition.sleep(333);
	}

	@Override
	public boolean valid() {
		return tab() > 0;
	}

	private int tab() {
		return ctx.varpbits.varpbit(1021) & 0xf;
	}
}
