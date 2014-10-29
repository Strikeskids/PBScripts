package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.cache.loader.rt4.wrappers.NpcDefinition;
import org.logicail.cache.loader.rt4.wrappers.ObjectDefinition;
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
	private static final String NOW_YOU_HAVE_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE = "Now you have runes you should see the Wind Strike";
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

		if (!npc().valid() || npc().tile().distanceTo(ctx.players.local()) > 10) {
			enter();
			return;
		}

		switch (stage()) {
			case 18:
				break; // talk

			case 20:
				if (ctx.chat.visible(NOW_YOU_HAVE_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE, NOW_YOU_HAVE_SOME_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE)) {
					if (ctx.game.tab() != Game.Tab.MAGIC) {
						ctx.game.tab(Game.Tab.MAGIC);
					}

					final Tile spellFrom = spellLocation();
					if (ctx.players.local().tile().y() != spellFrom.y()) {
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
					}).select(NpcDefinition.name(ctx, "Chicken")).each(Interactive.doSetBounds(BOUNDS_CHICKEN)).nearest().limit(2).shuffle().poll();

					if (ctx.camera.combineCamera(chicken, Random.nextInt(0, 50))) {
						final Component windStrike = ctx.widgets.widget(192).component(1);
						if (windStrike.valid()) {
							if (windStrike.click()) {
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
				break;
		}

		leave();

		super.run();
	}

	@Override
	protected void enter() {
		final GameObject table = table();
		if (table != ctx.objects.nil() && table.valid()) {
			ctx.movement.myWalk(table.tile().derive(-3, 0).derive(Random.nextInt(-3, 3), Random.nextInt(-3, 3)));
			Condition.sleep(200);
		} else {
			// Failsafe for region not loaded
			GameObject altar = ctx.objects.select().select(ObjectDefinition.name(ctx, "Altar")).nearest().poll();
			if (altar.valid()) {
				ctx.movement.myWalk(altar.tile().derive(Random.nextInt(8, 12), Random.nextInt(-22, -18)));
				Condition.sleep(200);
			}
		}
	}

	@Override
	protected void leave() {
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
	}

	private Tile spellLocation() {
		final GameObject table = table();
		if (table != ctx.objects.nil() && table.valid()) {
			return table.tile().derive(-3, 4);
		}
		return Tile.NIL;
	}

	private GameObject table() {
		final GameObject dragonshead = ctx.objects.select().select(ObjectDefinition.name(ctx, "Dragon's head")).poll();
		if (dragonshead.valid()) {
			return ctx.objects.select().select(ObjectDefinition.name(ctx, "Table")).nearest(dragonshead).poll();
		}
		return ctx.objects.nil();
	}

	private void walkSpellFrom() {
		final Tile derive = spellLocation().derive(Random.nextInt(-1, 1), 0);
		ctx.camera.prepare(derive.matrix(ctx));
		if (derive.matrix(ctx).inViewport()) {
			derive.matrix(ctx).interact("Walk here");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !ctx.movement.destination().equals(derive) || ctx.players.local().tile().equals(derive);
				}
			}, 300, 10);
			Condition.sleep(250);
		} else {
			log.info("MYWALK!");
			ctx.movement.myWalk(derive);
		}
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible(JUST_FOLLOW_THE_PATH_TO_THE_WIZARD_S_HOUSE, NOW_YOU_HAVE_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE, NOW_YOU_HAVE_SOME_RUNES_YOU_SHOULD_SEE_THE_WIND_STRIKE);
	}
}
