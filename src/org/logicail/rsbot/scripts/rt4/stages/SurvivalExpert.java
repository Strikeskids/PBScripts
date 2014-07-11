package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 13:09
 */
public class SurvivalExpert extends Talker {
	public SurvivalExpert(IClientContext ctx) {
		super(ctx, "Survival Expert");
	}

	public static int[] BOUNDS_GATE = {96, 128, -128, 0, -120, 128};

	@Override
	public void run() {
		if (tryContinue()) return;

		if (ctx.chat.visible("Well done, you've just cooked your first")) {
			final GameObject gate = ctx.objects.select().select(ObjectDefinition.name(ctx, "Gate")).each(Interactive.doSetBounds(BOUNDS_GATE)).nearest().poll();
			if (ctx.camera.prepare(gate) && gate.click("Open")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Follow the path until you get to the door with the yellow arrow");
					}
				}, 200, 50);
			}
			return;
		}

		if (ctx.chat.visible("Click on the flashing backpack icon to the")) {
			if (ctx.game.tab() != Game.Tab.INVENTORY) {
				ctx.game.tab(Game.Tab.INVENTORY);
			}
			return;
		}

		if (ctx.chat.visible("Click on the flashing bar graph icon")) {
			ctx.game.tab(Game.Tab.STATS);
			return;
		}

		if (!ctx.inventory.select().name("Raw shrimps").isEmpty() && ctx.chat.visible("Now you have caught some shrimp", "then use them on a fire")) {
			if (ctx.game.tab() != Game.Tab.INVENTORY) {
				ctx.game.tab(Game.Tab.INVENTORY);
			}
			cook();
			return;
		}

		if (ctx.chat.visible("Catch some Shrimp.", "Let's try cooking without burning")) {
			if (ctx.game.tab() != Game.Tab.INVENTORY) {
				ctx.game.tab(Game.Tab.INVENTORY);
			}
			ctx.inventory.select().name("Burnt shrimp").each(new Filter<Item>() {
				@Override
				public boolean accept(final Item item) {
					if (item.interact("Drop")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !item.valid();
							}
						}, 100, 10);
					}
					return true;
				}
			});

			final Npc fishingSpot = ctx.npcs.select().name("Fishing Spot").nearest().poll();
			if (ctx.camera.prepare(fishingSpot) && fishingSpot.click("Net")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().animation() == -1 && !ctx.inventory.select().name("Raw shrimps").isEmpty();
					}
				}, 200, 50);
			}
			return;
		}

		if (!ctx.chat.visible("all: making a fire") && ctx.chat.getComponentByText("Making a fire").visible()) {
			makeFire();
			return;
		}

//		if (ctx.chat.visible("To continue the tutorial go through that door")) {
//			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).nearest().poll();
//			if (ctx.camera.prepare(door) && door.click("Open", "Door")) {
//				Condition.wait(new Callable<Boolean>() {
//					@Override
//					public Boolean call() throws Exception {
//						return !valid();
//					}
//				}, 200, 25);
//			}
//			return;
//		}

		if (ctx.players.local().animation() == -1 && ctx.chat.visible("Cut down a tree", "Your character is now attempting to cut down the tree")) {
			chop();
			return;
		}

		super.run();
	}

	private void chop() {
		final GameObject tree = ctx.objects.select().select(ObjectDefinition.name(ctx, "Tree")).within(ctx.objects.select().select(ObjectDefinition.name(ctx, "Tree")).nearest().poll().tile().distanceTo(ctx.players.local()) + Random.nextInt(1, 5)).shuffle().poll();
		if (ctx.camera.prepare(tree)) {
			if (tree.click("Chop down", "Tree")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !tree.valid() || !ctx.inventory.select().name("Logs").isEmpty();
					}
				}, 333, 10);
			}
		}
	}

	private boolean cook() {
		final GameObject fire = ctx.objects.select().select(ObjectDefinition.name(ctx, "Fire")).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return gameObject.tile().matrix(ctx).reachable();
			}
		}).nearest().poll();
		if (!fire.valid()) {
			if (ctx.inventory.select().name("Logs").isEmpty()) {
				chop();
				return true;
			} else {
				makeFire();
			}
			return true;
		}

		final Item raw = ctx.inventory.select().name("Raw shrimps").shuffle().poll();
		if (ctx.camera.prepare(fire) && raw.valid()) {
			if (raw.click("Use")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.inventory.selectedItemIndex() > -1;
					}
				}, 200, 5)) {
					if (fire.interact("Use", "Raw shrimps -> Fire")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !raw.valid();
							}
						}, 200, 20);
					}
				}
			}
		}
		return false;
	}

	private void makeFire() {
		final Tile playerTile = ctx.players.local().tile();
		if (ctx.objects.select().at(playerTile).select(ObjectDefinition.name(ctx, "Fire")).isEmpty()) {
			final Item logs = ctx.inventory.select().name("Logs").shuffle().poll();
			final Item tinderbox = ctx.inventory.select().name("Tinderbox").shuffle().poll();
			if (logs.click("Use")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.inventory.selectedItemIndex() > -1;
					}
				}, 200, 5)) {
					if (tinderbox.click("Use", "Logs -> Tinderbox")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.chat.visible("Click on the flashing bar graph icon");
							}
						}, 200, 20);
					}
				}
			}
		} else {
			LogicailArea area = new LogicailArea(playerTile.derive(-3, -3), playerTile.derive(4, 4));
			final ArrayList<Tile> tiles = new ArrayList<Tile>(area.getReachable(ctx));
			Collections.shuffle(tiles);
			for (final Tile tile : tiles) {
				if (!ctx.objects.select().at(tile).name("Fire").isEmpty()) {
					continue;
				}

				if (ctx.movement.myWalk(tile)) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !ctx.players.local().inMotion() || ctx.players.local().tile().equals(tile);
						}
					}, 200, 10);
				}
				return;
			}
		}
	}
}
