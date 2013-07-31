package org.logicail.scripts.logartisanarmourer.tasks.swords;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 12:00
 */
public class HeatIngots extends Node {
	public static final int[] INGOT_IDS_IV = {20647, 20648, 20649, 20650, 20651, 20652};
	public static final int FURNACE = 24720;

	public HeatIngots(LogicailMethodContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(INGOT_IDS_IV).isEmpty()
				&& !MakeSword.isOpen(ctx)
				&& !ctx.backpack.select().id(MakeSword.TONGS).isEmpty();
	}

	@Override
	public void execute() {
		if (ctx.skillingInterface.getCategory().equals("heat ingots")) {
			if (ctx.skillingInterface.start()) {
				ctx.waiting.wait(2000, new Condition() {
					@Override
					public boolean validate() {
						return !ctx.backpack.select().id(MakeSword.HEATED_INGOTS).isEmpty();
					}
				});
			}
		} else {
			for (GameObject furnace : ctx.objects.select().id(FURNACE).nearest().first()) {
				if (ctx.interaction.prepare(furnace) && ctx.interaction.interact(furnace, "Smelt", "Furnace")) {
					ctx.waiting.wait(3500, new Condition() {
						@Override
						public boolean validate() {
							return ctx.skillingInterface.isOpen();
						}
					});
				}
			}
		}
	}
}
