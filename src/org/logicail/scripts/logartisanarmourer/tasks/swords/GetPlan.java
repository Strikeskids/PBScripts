package org.logicail.scripts.logartisanarmourer.tasks.swords;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.wrappers.Npc;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 28/07/13
 * Time: 12:00
 */
public class GetPlan extends Node {
	public static final int[] EGIL_ABEL = {6642, 6647};
	private final LogArtisanArmourerOptions options;

	public GetPlan(LogicailMethodContext ctx) {
		super(ctx);
		this.options = ((LogArtisanArmourer) ctx.script).options;
	}

	@Override
	public boolean activate() {
		return options.finishedSword || !options.gotPlan;
	}

	@Override
	public void execute() {
		if (!ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty()) {
			options.finishedSword = false;
			options.gotPlan = true;
			return;
		}

		//options.status = "Talk to Egil/Abel";

		if (MakeSword.isOpen(ctx)) {
			MakeSword.closeInterface(ctx);
			return;
		}

		final int xp = ctx.skills.getExperience(Skills.SMITHING);

		for (Npc npc : ctx.npcs.select().id(EGIL_ABEL).nearest().first()) {
			if (ctx.interaction.prepare(npc)) {
				sleep(100, 600);
				if (npc.hover()) {
					sleep(100, 600);
					final String[] actions = npc.getActions();
					if (actions.length > 1 && ctx.interaction.interact(npc, actions[1])) {
						ctx.waiting.wait(3500, new Condition() {
							@Override
							public boolean validate() {
								return !ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty() || xp != ctx.skills.getExperience(Skills.SMITHING);
							}
						});
						sleep(200, 1200);
					}
				}
			}
		}
	}
}
