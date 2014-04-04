package org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect;

import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.*;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 16:35
 */
public class Ancestors extends RespectTask {
	private static final int[] ANCESTOR_IDS = {6657, 6658, 6659, 6660, 6661, 6662};

	public Ancestors(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		// 1% every 9 killed
		return "Kill Ancestors";
	}

	private MobileIdNameQuery<Npc> getAncestor() {
		return ctx.npcs.select().select(new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				if (Arrays.binarySearch(ANCESTOR_IDS, npc.id()) >= 0) {
					final Actor interacting = npc.interacting();
					return (interacting == null || !npc.inCombat() || (interacting.equals(ctx.players.local()) && npc.healthPercent() > 0)) && ctx.movement.distance(options.getAreaSmall().getCentralTile(), npc) < 50;
				}
				return false;
			}
		}).first();
	}

	@Override
	public boolean valid() {
		if (super.valid()) {
			if (!getAncestor().isEmpty()) {
				return !updateAbilities().isEmpty();
			}

			if (ctx.combatBar.expanded()) {
				ctx.sleep(600);
				if (ctx.combatBar.expanded(false)) {
					ctx.sleep(600);
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		for (Npc ancestor : ctx.npcs) {
			if (ctx.camera.prepare((ancestor))) {
				options.status = "Attacking ancestor";
				options.isSmithing = false;
				boolean fought = false;

				final Player local = ctx.players.local();
				if (!local.inCombat() && ancestor.interact("Attack", ancestor.name())) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return local.inCombat();
						}
					});
				}

				final Timer t = new Timer(Random.nextInt(5000, 10000));

				while (t.running()) {
					if (!local.inMotion() && (!ancestor.valid() || (ancestor.interacting() != null && !ancestor.interacting().equals(local)))) {
						break;
					}
					final IdQuery<Action> actions = updateAbilities();

					if (local.inCombat()) {
						fought = true;
						// Find an ability
						for (Action action : actions.shuffle()) {
							if (action.valid() && action.ready() && action.select()) {
								ctx.sleep(500);
								break;
							}
						}
						t.reset();
					}
					ctx.sleep(50);
				}

				if (fought) {
					ctx.sleep(500);
				}
			}
		}
	}

	private IdQuery<Action> updateAbilities() {
		return ctx.combatBar.select().select(new Filter<Action>() {
			@Override
			public boolean accept(Action action) {
				return action.type() == Action.Type.ABILITY;
			}
		});
	}
}

