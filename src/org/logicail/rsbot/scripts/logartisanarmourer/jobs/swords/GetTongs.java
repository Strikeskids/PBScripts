package org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.AbstractStrategy;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/12/12
 * Time: 11:28
 */
public class GetTongs extends AbstractStrategy {
	public GetTongs(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "Get Tongs";
	}

	public static final int TONGS_WORKBENCH = 29393;

	@Override
	public boolean activate() {
		return super.activate()
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

		for (GameObject workbench : ctx.objects.select().id(TONGS_WORKBENCH).nearest().first()) {
			if (ctx.camera.myTurnTo(workbench) && workbench.interact("Take", "Workbench")) {
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
