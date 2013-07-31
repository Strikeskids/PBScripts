package org.logicail.scripts.logartisanarmourer.tasks.respect;

import org.logicail.api.filters.CombatFilter;
import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.providers.Condition;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Actor;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:20
 */
public class KillAncestors extends RespectNode {
	private static final int[] ANCESTOR_IDS = {6657, 6658, 6659, 6660, 6661, 6662};

	public KillAncestors(LogicailMethodContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return super.activate() && getAncestor() && !updateAbilities().isEmpty();
	}

	private boolean getAncestor() {
		return !ctx.npcs.select().id(ANCESTOR_IDS).select(new CombatFilter<Npc>(ctx)).nearest().first().isEmpty();
	}

	@Override
	public void execute() {
		for (final Npc target : ctx.npcs) {
			if (!ctx.interaction.prepare(target)) {
				ctx.movement.stepTowards(target.getLocation().randomize(2, 2));
				ctx.waiting.wait(3000, new Condition() {
					@Override
					public boolean validate() {
						return target.isOnScreen();
					}
				});
			}

			if (ctx.interaction.prepare(target)) {
				final Player local = ctx.players.local();
				if (local.getInteracting() == null && target.interact("Attack", target.getName())) {
					ctx.waiting.wait(2500, new Condition() {
						@Override
						public boolean validate() {
							return local.isInCombat() || target.isInCombat();
						}
					});
				}

				Timer timer = new Timer(Random.nextInt(5000, 10000));

				while (timer.isRunning()) {
					if (!getAncestor()) {
						break;
					}

					Actor targetInteracting = target.getInteracting();
					if (targetInteracting != null && !targetInteracting.equals(local)) {
						break;
					}

					updateAbilities();

					for (Action action : ctx.combatBar.shuffle()) {
						if (action.isReady() && action.select()) {
							sleep(500, 1100);
							break;
						}
					}

					sleep(100, 300);

					timer.reset();
				}
				sleep(100, 300);
			}
		}
	}

	private IdQuery<Action> updateAbilities() {
		return ctx.combatBar.select(new Filter<Action>() {
			@Override
			public boolean accept(Action action) {
				return action.getType() == Action.Type.ABILITY && action.isReady();
			}
		});
	}
}
