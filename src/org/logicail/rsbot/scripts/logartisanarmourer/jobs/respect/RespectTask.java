package org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.rt6.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 17:17
 */
public abstract class RespectTask extends ArtisanArmourerTask {
	public final static int SETTING_RESPECT = 126;

	public RespectTask(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public boolean valid() {
		return super.valid()
				&& getRespect(ctx) < 100
				&& !ctx.skillingInterface.opened();
	}

	public static int getRespect(ClientContext ctx) {
		final int varpbit = ctx.varpbits.varpbit(SETTING_RESPECT, 0, 0x1ffff);
		return varpbit < 1000 ? 0 : varpbit / 1000;
	}
}
