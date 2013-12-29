package org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.methods.Game;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 17:17
 */
public abstract class RespectTask extends ArtisanArmourerTask {
	public final static int SETTING_RESPECT = 126;

	public RespectTask(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& ctx.game.getClientState() == Game.INDEX_MAP_LOADED
				&& getRespect() < 100
				&& !ctx.skillingInterface.isOpen();
	}

	public int getRespect() {
		return ctx.settings.get(SETTING_RESPECT, 10, 0x7f);
	}
}
