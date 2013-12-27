package org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Actor;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Player;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 16:35
 */
public class Ancestors extends RespectStrategy {
	private static final int[] ANCESTOR_IDS = {6657, 6658, 6659, 6660, 6661, 6662};

	public Ancestors(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public String toString() {
		// 1% every 9 killed
		return "Kill Ancestors";
	}

	@Override
	public boolean activate() {
		if (super.activate()) {
			if (!getAncestor().isEmpty()) {
				return !updateAbilities().isEmpty();
			}

			if (ctx.combatBar.isExpanded()) {
				sleep(500, 1500);
				if (ctx.combatBar.setExpanded(false)) {
					sleep(500, 1500);
				}
			}
		}
		return false;
	}

	private IdQuery<Action> updateAbilities() {
		return ctx.combatBar.select(new Filter<Action>() {
			@Override
			public boolean accept(Action action) {
				return action.getType() == Action.Type.ABILITY;
			}
		});
	}

	private BasicNamedQuery<Npc> getAncestor() {
		return ctx.npcs.select(new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				if (Arrays.binarySearch(ANCESTOR_IDS, npc.getId()) >= 0) {
					final Actor interacting = npc.getInteracting();
					return (interacting == null || !npc.isInCombat() || (interacting.equals(ctx.players.local()) && npc.getHealthPercent() > 0)) && ctx.movement.getDistance(LogArtisanArmourer.getAreaSmall().getCentralTile(), npc) < 50;
				}
				return false;
			}
		}).first();
	}

	@Override
	public void run() {
		for (Npc ancestor : getAncestor()) {
			if (ctx.camera.myTurnTo((ancestor))) {
				LogArtisanArmourer.status = "Attacking ancestor";
				LogArtisanArmourer.isSmithing = false;
				boolean fought = false;

				final Player local = ctx.players.local();
				if (!local.isInCombat() && ancestor.interact("Attack", ancestor.getName())) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return local.isInCombat();
						}
					});
				}

				final Timer t = new Timer(Random.nextInt(5000, 10000));

				while (t.isRunning()) {
					if (!local.isInMotion() && (!ancestor.isValid() || (ancestor.getInteracting() != null && !ancestor.getInteracting().equals(local)))) {
						break;
					}
					final IdQuery<Action> actions = updateAbilities();

					if (local.isInCombat()) {
						fought = true;
						// Find an ability
						for (Action action : actions.shuffle()) {
							if (action.isValid() && action.isReady() && action.select()) {
								sleep(250, 1000);
								break;
							}
						}
						t.reset();
					}
					sleep(33, 100);
				}

				if (fought) {
					sleep(500, 1000);
				}
			}
		}
	}
}

