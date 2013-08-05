package org.logicail.scripts.logartisanarmourer.tasks.swords;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.Shutdown;
import org.logicail.api.methods.providers.ChatOption;
import org.logicail.api.methods.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 11:44
 */
public class GetTongs extends Node {
	public static final int TONGS_WORKBENCH = 29393;

	public GetTongs(LogicailMethodContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return ctx.backpack.select().id(MakeSword.TONGS).isEmpty();
	}

	@Override
	public void execute() {
		if (ctx.backpack.isFull()) {
			new Shutdown(ctx).error("Need Tongs but backpack is full", true);
			return;
		}

		// TODO: Use ChatAPI in 5010 https://github.com/powerbot/RSBot-Issues/issues/161
		ChatOption chatOption = ctx.chatOptions.getOption("Tongs");
		if (chatOption != null) {
			if (chatOption.select(Random.nextBoolean()) >= 0) {
				ctx.waiting.wait(2500, new Condition() {
					@Override
					public boolean validate() {
						return !activate();
					}
				});
				sleep(200, 1200);
			}
			return;
		}

		for (GameObject table : ctx.objects.select().id(TONGS_WORKBENCH).nearest().first()) {
			if (ctx.interaction.interact(table, "Take", "Workbench")) {
				ctx.waiting.wait(4000, new Condition() {
					@Override
					public boolean validate() {
						return ctx.chatOptions.getOption("Tongs") != null;
					}
				});
			}
		}
	}
}
