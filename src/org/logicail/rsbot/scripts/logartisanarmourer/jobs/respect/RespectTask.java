package org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 17:17
 */
public abstract class RespectTask extends ArtisanArmourerTask {
	public final static int SETTING_RESPECT = 126;

	public RespectTask(LogArtisanArmourer script) {
		super(script);
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& getRespect() < 100
				&& !ctx.skillingInterface.isOpen();
	}

	public int getRespect() {
		return ctx.settings.get(SETTING_RESPECT, 10, 0x7f);
	}
}
