package org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Menu;
import org.powerbot.script.rt6.Npc;
import org.powerbot.script.rt6.Skills;

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
	private final Filter<Menu.Command> interactFilter = new Filter<Menu.Command>() {
		@Override
		public boolean accept(Menu.Command entry) {
			final String action = entry.action;
			return action.equals("Quick-score") || action.equals("Get-plans");
		}
	};

	public GetPlan(LogArtisanWorkshop script, MakeSword makeSword) {
		super(script);
		this.makeSword = makeSword;
	}

	@Override
	public String toString() {
		return "Interact with Egil/Abel";
	}

	@Override
	public boolean valid() {
		return super.valid()
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

		final int xp = ctx.skills.experience(Skills.SMITHING);

		for (Npc egil : ctx.npcs.select().id(EGIL_ABEL).nearest()/*.limit(3).shuffle()*/.first()) {
			if (ctx.camera.prepare(egil) && egil.interact(interactFilter)) {
				ctx.sleep(500);
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty() || xp != ctx.skills.experience(Skills.SMITHING);
					}
				}, Random.nextInt(300, 600), Random.nextInt(9, 12));
				ctx.sleep(500);
			}
		}
	}
}
