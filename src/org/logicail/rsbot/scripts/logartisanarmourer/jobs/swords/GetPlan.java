package org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
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
	private final MakeSword makeSword;

	public GetPlan(LogArtisanWorkshop script, MakeSword makeSword) {
		super(script);
		this.makeSword = makeSword;
	}

	@Override
	public String toString() {
		return "Interact with Egil/Abel";
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& options.finishedSword || !options.gotPlan;
	}

	@Override
	public void run() {
		if (!ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty()) {
			options.finishedSword = false;
			options.gotPlan = true;
			return;
		}

		options.status = "Talk to Egil/Abel";

		if (makeSword.isOpen()) {
			makeSword.closeInterface();
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
