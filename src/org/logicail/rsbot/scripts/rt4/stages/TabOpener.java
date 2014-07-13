package org.logicail.rsbot.scripts.rt4.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/07/2014
 * Time: 23:52
 */
public class TabOpener extends GraphScript.Action<IClientContext> {
	private final static String[] TAB_STRINGS;

	private static final HashMap<String, Game.Tab> TAB_MAPPING;

	static {
		TAB_MAPPING = new HashMap<String, Game.Tab>();
		TAB_MAPPING.put("Click on the flashing icon to open the Prayer", Game.Tab.PRAYER);
		TAB_MAPPING.put("smiling face to open your friends list", Game.Tab.FRIENDS_LIST);
		TAB_MAPPING.put("on the other flashing face to the right", Game.Tab.IGNORED_LIST);
		TAB_MAPPING.put("icon of a man, the one to the right of your backpack", Game.Tab.EQUIPMENT);
		TAB_MAPPING.put("Click on the flashing crossed swords", Game.Tab.ATTACK);
		TAB_MAPPING.put("Open up the Magic menu by", Game.Tab.MAGIC);
		TAB_MAPPING.put("and even kebabs. Now you've got the hang", Game.Tab.EMOTES);
		TAB_MAPPING.put("Open the Quest Journal.", Game.Tab.QUESTS);
		TAB_MAPPING.put("You will notice the flashing icon of a spanner", Game.Tab.OPTIONS);
		TAB_MAPPING.put("Please click on the flashing spanner icon", Game.Tab.OPTIONS);
		TAB_MAPPING.put("It's only a short distance to the next guide.", Game.Tab.OPTIONS);
		TAB_MAPPING.put("Click on the flashing backpack icon to the right hand side of", Game.Tab.INVENTORY);
		TAB_MAPPING.put("Click on the flashing bar graph icon", Game.Tab.STATS);
		TAB_MAPPING.put("Well done! Your first load fo bread. As you gain experience in", Game.Tab.MUSIC);

		Set<String> set = TAB_MAPPING.keySet();
		TAB_STRINGS = set.toArray(new String[set.size()]);
	}

	public TabOpener(IClientContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		for (Map.Entry<String, Game.Tab> entry : TAB_MAPPING.entrySet()) {
			if (ctx.chat.visible(entry.getKey())) {
				final Game.Tab tab = entry.getValue();
				if (tab == Game.Tab.IGNORED_LIST) {
					final Component component = findTextureId(905);
					if (component != null && component.click("Ignore List")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() {
								return ctx.game.tab() == Game.Tab.IGNORED_LIST;
							}
						}, 50, 10);
					}
				} else {
					ctx.game.tab(tab);
				}
				Condition.sleep(250);
				return;
			}
		}
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
		return ctx.chat.visible(TAB_STRINGS);
	}
}
