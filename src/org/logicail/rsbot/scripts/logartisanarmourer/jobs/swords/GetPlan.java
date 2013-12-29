package org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Npc;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/12/12
 * Time: 11:16
 */
public class GetPlan extends ArtisanArmourerTask {
	public static final int[] EGIL_ABEL = {6642, 6647};

	public GetPlan(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "Interact with Egil/Abel";
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& MakeSword.finishedSword || !LogArtisanArmourer.gotPlan;
	}

	@Override
	public void run() {
		if (!ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty()) {
			MakeSword.finishedSword = false;
			LogArtisanArmourer.gotPlan = true;
			return;
		}

		LogArtisanArmourer.status = "Talk to Egil/Abel";

		if (MakeSword.get().isOpen()) {
			MakeSword.get().closeInterface();
			return;
		}

		final int xp = ctx.skills.getExperience(Skills.SMITHING);

		for (Npc egil : ctx.npcs.select().id(EGIL_ABEL).nearest().first()) {
			if (ctx.camera.prepare(egil) && egil.click(false) && ctx.menu.isOpen()) {
				final String[] actions = egil.getActions();
				sleep(250, 1000);
				if (actions.length > 1 && actions[1] != null && egil.interact(actions[1])) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty() || xp != ctx.skills.getExperience(Skills.SMITHING);
						}
					});
				}
			}
		}
	}
}
