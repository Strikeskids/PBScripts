package org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/12/12
 * Time: 11:02
 */
public class HeatIngots extends ArtisanArmourerTask {
	public static final int[] INGOT_IDS_IV = {20647, 20648, 20649, 20650, 20651, 20652};
	public static final int FURNACE = 24720;

	public HeatIngots(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "Heat Ingots";
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& !ctx.backpack.select().id(INGOT_IDS_IV).isEmpty()
				&& !MakeSword.get().isOpen()
				&& !ctx.backpack.select().id(MakeSword.TONGS).isEmpty();
	}

	@Override
	public void run() {
		//System.out.println("[" + SkillingInterface.getCategory() + "]");
		if (ctx.skillingInterface.getCategory().equalsIgnoreCase("heat ingots")) {
			ctx.skillingInterface.start();
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !ctx.backpack.select().id(MakeSword.HEATED_INGOTS).isEmpty();
				}
			});
		} else {
			for (GameObject furnace : ctx.objects.select().id(FURNACE).nearest().first()) {
				if (ctx.camera.prepare(furnace) && furnace.interact("Smelt", "Furnace")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.skillingInterface.isOpen();
						}
					});
				}
			}
		}
	}
}
