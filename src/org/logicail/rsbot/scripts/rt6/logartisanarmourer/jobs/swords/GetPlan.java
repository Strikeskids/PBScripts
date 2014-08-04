package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ChatOption;
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
	private static final String[] CHAT_TEXT = {
			"Would you like to have that sword you made?",
			"Ah, the order for this sword has been cancelled.",
			"Yes please",
			"No, thanks"
	};
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
				&& options.finishedSword || !options.gotPlan.get();
	}

	@Override
	public void run() {
		if (makeSword.isOpen()) {
			options.status = "Close sword interface";
			makeSword.closeInterface();
			return;
		}

		if (ctx.chat.queryContinue()) {
			options.status = "Continue";
			for (final String text : CHAT_TEXT) {
				if (ctx.chat.isTextVisible(text)) {
					if (ctx.chat.clickContinue(Random.nextBoolean())) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.chat.isTextVisible(text);
							}
						}, 150, 10);
					}
					return;
				}
			}
			ctx.chat.clickContinue(Random.nextBoolean());
			sleep(250);
			return;
		}

		if (!ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty()) {
			options.finishedSword = false;
			options.gotPlan.set(true);
			return;
		}

		options.status = "Talk to Egil/Abel";

		ChatOption option = ctx.chat.select().text("No, thanks.").poll();
		if (option.valid()) {
			if (option.select(Random.nextBoolean())) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.queryContinue();
					}
				}, 150, 10);
			}
			return;
		}

		final int xp = ctx.skills.experience(Skills.SMITHING);

		Npc egil = ctx.npcs.select().id(EGIL_ABEL).nearest()/*.limit(3).shuffle()*/.poll();
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
