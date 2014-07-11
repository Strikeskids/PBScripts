package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.ActionManifest;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 21:41
 */
@ActionManifest(name = "Magic Instructor")
public class MagicInstructor extends Talker {
	public MagicInstructor(IClientContext ctx) {
		super(ctx, "Magic Instructor");
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible("Just follow the path to the Wizard's house");
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		if (ctx.chat.visible("Just follow the path to the Wizard's house") && !npc().valid()) {
			final GameObject table = table();
			if (table.valid()) {
				ctx.movement.myWalk(table.tile().derive(-3, 0));
				Condition.sleep(200);
			}
			return;
		}

		if (ctx.chat.visible("Open up the Magic menu by")) {
			ctx.game.tab(Game.Tab.MAGIC);
			return;
		}

		if (ctx.chat.visible("Now you have runes you should see the Wind Strike")) {
			if (ctx.game.tab() != Game.Tab.MAGIC) {
				ctx.game.tab(Game.Tab.MAGIC);
			}

			final GameObject table = table();
			if (table.valid()) {
				final Tile spellFrom = table.tile().derive(-3, -4);
				final int distance = ctx.movement.distance(spellFrom, ctx.players.local());
				if (distance > 2) {
					final Tile derive = spellFrom.derive(Random.nextInt(-1, 1), 0);
					ctx.camera.turnTo(derive);
					if (derive.matrix(ctx).inViewport()) {
						derive.matrix(ctx).interact("Walk here");
						Condition.sleep(500);
					} else {
						ctx.movement.myWalk(derive);
					}
					return;
				}
			}

			final Npc chicken = ctx.npcs.select().name("Chicken").select(new Filter<Npc>() {
				@Override
				public boolean accept(Npc npc) {
					return !npc.inCombat();
				}
			}).nearest().poll();

			if (ctx.camera.combineCamera(chicken, Random.nextInt(0, 50))) {
				final Component windStrike = ctx.widgets.widget(192).component(1);
				if (windStrike.valid()) {
					if (windStrike.click("Cast")) {
						Condition.sleep(250);
						chicken.interact("Cast", "Wind Strike -> Chicken");
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.chat.visible("All you need to do now is move on to the mainland");
							}
						}, 200, 5);
					} else {
						windStrike.interact("Cancel");
						Condition.sleep(200);
					}
				}
			}
			return;
		}

		final Component leave = ctx.chat.getComponentByText("Yes.");
		if (leave.valid()) {
			leave.click("Continue");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.chat.queryContinue();
				}
			}, 200, 20);
		}

		super.run();
	}

	private GameObject table() {
		return ctx.objects.select().select(ObjectDefinition.name(ctx, "Table")).nearest(ctx.objects.select().select(ObjectDefinition.name(ctx, "Dragon's head")).poll()).poll();
	}
}
