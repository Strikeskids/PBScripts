package org.logicail.scripts.logartisanarmourer.tasks.swords;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.providers.ChatOption;
import org.logicail.api.providers.Condition;
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
		return !ctx.backpack.isFull()
				&& ctx.backpack.select().id(MakeSword.TONGS).isEmpty();
	}

	@Override
	public void execute() {
		ChatOption chatOption = ctx.chatOptions.getOption("Tongs");
		if (chatOption != null) {
			if (chatOption.select(Random.nextBoolean()) >= 0) {
				ctx.waiting.wait(2000, new Condition() {
					@Override
					public boolean validate() {
						return !ctx.backpack.select().id(MakeSword.TONGS).isEmpty();
					}
				});
			}
			return;
		}

		for (GameObject table : ctx.objects.select().id(TONGS_WORKBENCH).nearest().first()) {
			if (ctx.interaction.prepare(table) && ctx.interaction.interact(table, "Take", "Workbench")) {
				ctx.waiting.wait(3000, new Condition() {
					@Override
					public boolean validate() {
						return ctx.chatOptions.getOption("Tongs") != null;
					}
				});
			}
		}
	}
}
