package org.logicail.rsbot.scripts.framework.context.rt4.providers;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/07/2014
 * Time: 14:09
 */
public class IGame extends Game {
	public IGame(ClientContext ctx) {
		super(ctx);
	}

	@Override
	public boolean tab(final Tab tab) {
		final Component component = tabByTextureId(tab.texture);
		return tab() == tab || component != null && component.click(tab.tip) && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return tab() == tab;
			}
		}, 50, 10);

	}

	private Component tabByTextureId(int texture) {
		for (Component component : this.ctx.widgets.widget(548).components()) {
			if (component.textureId() == texture) {
				return component;
			}
		}

		return null;
	}
}
