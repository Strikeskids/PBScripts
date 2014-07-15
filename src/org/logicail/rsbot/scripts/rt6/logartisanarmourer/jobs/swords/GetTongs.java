package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ChatOption;
import org.powerbot.script.rt6.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/12/12
 * Time: 11:28
 */
public class GetTongs extends ArtisanArmourerTask {
	public static final int TONGS_WORKBENCH = 29393;

	public GetTongs(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Get Tongs";
	}

	@Override
	public boolean valid() {
		return super.valid()
				&& !ctx.backpack.isFull()
				&& ctx.backpack.select().id(MakeSword.TONGS).isEmpty();
	}

	@Override
	public void run() {
		for (ChatOption option : ctx.chat.select().text("Tongs").first()) {
			if (option.select(Random.nextBoolean())) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.backpack.select().id(MakeSword.TONGS).isEmpty();
					}
				});
				return;
			}
		}

		for (GameObject workbench : ctx.objects.select().id(TONGS_WORKBENCH).nearest().limit(3).shuffle().first()) {
			if (ctx.camera.prepare(workbench) && workbench.interact("Take", "Workbench")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.chat.select().text("Tongs").isEmpty();
					}
				});
			}
		}
	}
}
