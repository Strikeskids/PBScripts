package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.NpcDefinition;
import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 21:41
 */
public class MagicInstructor extends Talker {
	private static final String JUST_FOLLOW_THE_PATH_TO_THE_WIZARD_S_HOUSE = "Just follow the path to the Wizard's house";
	private static final String NOW_YOU_HAVE_SOME_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE = "Now you have some runes you should see the Wind Strike";
	private static final int[] BOUNDS_CHICKEN = {-40, 40, -48, 0, -40, 40};
	private final HashSet<Tile> ignored = new HashSet<Tile>();

	public MagicInstructor(IClientContext ctx) {
		super(ctx, "Magic Instructor");
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		if (ctx.chat.visible(JUST_FOLLOW_THE_PATH_TO_THE_WIZARD_S_HOUSE) && !npc().valid()) {
			final GameObject table = table();
			if (table.valid()) {
				ctx.movement.myWalk(table.tile().derive(-3, 0));
				Condition.sleep(200);
			}
			return;
		}

		if (ctx.chat.visible(NOW_YOU_HAVE_SOME_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE)) {
			if (ctx.game.tab() != Game.Tab.MAGIC) {
				ctx.game.tab(Game.Tab.MAGIC);
			}

			final Tile spellFrom = spellLocation();
			final int distance = ctx.movement.distance(spellFrom, ctx.players.local());
			if (distance > 2) {
				walkSpellFrom();
				return;
			}

			final Npc chicken = ctx.npcs.select().select(new Filter<Npc>() {
				@Override
				public boolean accept(Npc npc) {
					return !ignored.contains(npc.tile());
				}
			}).select(new Filter<Npc>() {
				@Override
				public boolean accept(Npc npc) {
					return !npc.inCombat();
				}
			}).select(NpcDefinition.filter(ctx, "Chicken")).each(Interactive.doSetBounds(BOUNDS_CHICKEN)).nearest().limit(2).shuffle().poll();

			if (ctx.camera.combineCamera(chicken, Random.nextInt(0, 50))) {
				final Component windStrike = ctx.widgets.widget(192).component(1);
				if (windStrike.valid()) {
					if (windStrike.click("Cast")) {
						Condition.sleep(250);
						if (chicken.interact("Cast", "Wind Strike -> Chicken")) {
							final Tile ignore = chicken.tile();
							final Tile tile = spellLocation();
							if (Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									final Tile destination = ctx.movement.destination();
									return destination != Tile.NIL && ctx.movement.distance(tile, destination) > 3;
								}
							}, 100, 5)) {
								if (chicken.tile().equals(ignore)) {
									ignored.add(ignore);
								}
								Condition.sleep(200);
								walkSpellFrom();
								Condition.sleep(200);
							}
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ctx.chat.visible("All you need to do now is move on to the mainland");
								}
							}, 200, 5);
						} else {
							Condition.sleep(500);
						}
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

	private Tile spellLocation() {
		final GameObject table = table();
		if (table.valid()) {
			return table.tile().derive(-4, 7);
		}
		return Tile.NIL;
	}

	private GameObject table() {
		return ctx.objects.select().select(ObjectDefinition.name(ctx, "Table")).nearest(ctx.objects.select().select(ObjectDefinition.name(ctx, "Dragon's head")).poll()).poll();
	}

	private void walkSpellFrom() {
		final Tile derive = spellLocation().derive(Random.nextInt(-1, 1), Random.nextInt(0, 2));
		ctx.camera.prepare(derive.matrix(ctx));
		if (derive.matrix(ctx).inViewport()) {
			derive.matrix(ctx).interact("Walk here");
			Condition.sleep(500);
		} else {
			log.info("MYWALK!");
			ctx.movement.myWalk(derive);
		}
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible(JUST_FOLLOW_THE_PATH_TO_THE_WIZARD_S_HOUSE, NOW_YOU_HAVE_SOME_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE);
	}
}
