package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import com.logicail.loader.rt4.wrappers.NpcDefinition;
import com.logicail.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.logicail.rsbot.scripts.framework.util.LoopCondition;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
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

	private static final int SHRIMP = 315;
	private static final int RAW_SHRIMP = 2514;
	private static final int BURNT_SHRIMP = 7954;
	private static final int LOGS = 2511;
	private static final int SMALL_FISHING_NET = 303;
	private static final int BRONZE_AXE = 1351;
	private static final int TINDERBOX = 590;
	private static final int[] BOUNDS_FISHING_SPOT = {-48, 40, -8, 0, -52, 36};
	private static final int[] TREE_BOUNDS = {-32, 32, -240, 0, -28, 48};

	public SurvivalExpert(RT4ClientContext ctx) {
		super(ctx, "Survival Expert");
	}

	public static int[] BOUNDS_GATE = {96, 128, -128, 0, -120, 128};

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		switch (stage()) {
			case 2:
				if (!ctx.inventory.select().id(TINDERBOX).isEmpty() && !ctx.inventory.select().id(LOGS).isEmpty()) {
					makeFire();
					return;
				}

				if (!ctx.inventory.select().id(BRONZE_AXE).isEmpty() && !ctx.chat.visible("Your skill stats.")) {
					chop();
					return;
				}

				break;
			case 3:
				ctx.inventory.select().id(BURNT_SHRIMP).each(new Filter<Item>() {
					@Override
					public boolean accept(final Item item) {
						ctx.game.tab(Game.Tab.INVENTORY);
						ctx.inventory.deselect();
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

				if (!ctx.inventory.select().id(SMALL_FISHING_NET).isEmpty() && ctx.inventory.select().id(SHRIMP).isEmpty()) {
					if (!ctx.inventory.select().id(RAW_SHRIMP).isEmpty()) {
						cook();
						return;
					}

					final Npc fishingSpot = ctx.npcs.select().select(NpcDefinition.name(ctx, "Fishing spot")).each(Interactive.doSetBounds(BOUNDS_FISHING_SPOT)).nearest().limit(2).shuffle().poll();
					if (ctx.camera.prepare(fishingSpot) && fishingSpot.interact("Net")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.players.local().animation() == -1 && !ctx.inventory.select().id(RAW_SHRIMP).isEmpty();
							}
						}, 200, 50);
					}
					return;
				}

				leave();
				return;
			default:
				leave();
				return;
		}

		super.run();
	}

	@Override
	protected void enter() {

	}

	@Override
	protected void leave() {
		final GameObject gate = ctx.objects.select().select(ObjectDefinition.name(ctx, "Gate")).each(Interactive.doSetBounds(BOUNDS_GATE)).nearest().poll();
		if (ctx.camera.prepare(gate) && gate.interact("Open")) {
			LoopCondition.wait(new LoopCondition(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return stage() >= 4;
				}
			}, new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.players.local().inMotion();
				}
			}), 200, 10);
		}
	}

	private void chop() {
		final GameObject tree = ctx.objects.select().select(ObjectDefinition.name(ctx, "Tree")).nearest().select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject o) {
				return o.tile().derive(-1, 0).matrix(ctx).reachable()
						|| o.tile().derive(1, 0).matrix(ctx).reachable()
						|| o.tile().derive(0, 1).matrix(ctx).reachable()
						|| o.tile().derive(0, -1).matrix(ctx).reachable();
			}
		}).limit(3).each(Interactive.doSetBounds(TREE_BOUNDS)).shuffle().poll();
		if (ctx.camera.prepare(tree)) {
			if (tree.interact("Chop down", "Tree")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !tree.valid() || !ctx.inventory.select().id(LOGS).isEmpty();
					}
				}, 333, 10);
			}
		}
	}

	private boolean cook() {
		ctx.game.tab(Game.Tab.INVENTORY);

		final GameObject fire = ctx.objects.select().select(ObjectDefinition.name(ctx, "Fire")).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return gameObject.tile().matrix(ctx).reachable();
			}
		}).nearest().poll();
		if (!fire.valid()) {
			if (ctx.inventory.select().id(LOGS).isEmpty()) {
				chop();
				return true;
			} else {
				makeFire();
			}
			return true;
		}

		if (ctx.camera.prepare(fire) && ctx.inventory.select(RAW_SHRIMP)) {
			final Item item = ctx.inventory.selectedItem();
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.inventory.selectedItemIndex() > -1;
				}
			}, 200, 5)) {
				if (fire.interact("Use", item.name() + " -> Fire")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !item.valid();
						}
					}, 200, 20);
				}
			}

		}
		return false;
	}

	private void makeFire() {
		final Tile playerTile = ctx.players.local().tile();
		if (ctx.objects.select().at(playerTile).select(ObjectDefinition.name(ctx, "Fire")).isEmpty()) {
			if (ctx.inventory.itemOnItem(TINDERBOX, LOGS)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Click on the flashing bar graph icon");
					}
				}, 200, 20);
			}
		} else {
			LogicailArea area = new LogicailArea(playerTile.derive(-3, -3), playerTile.derive(4, 4));
			final ArrayList<Tile> tiles = new ArrayList<Tile>(area.getReachable(ctx));
			Collections.shuffle(tiles);
			for (final Tile tile : tiles) {
				if (!ctx.objects.select().at(tile).select(ObjectDefinition.name(ctx, "Fire")).isEmpty()) {
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
