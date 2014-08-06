package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import com.logicail.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.Rocks;
import org.logicail.rsbot.scripts.rt4.ostutorialisland.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.*;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 18:18
 */
public class MiningInstructor extends Talker {
	private static final int BRONZE_BAR = 2349;
	private static final int TIN_ORE = 438;
	private static final int COPPER_ORE = 436;
	private static final int BRONZE_DAGGER = 1205;
	private static final int BRONZE_PICKAXE = 1265;
	private static final int HAMMER = 2347;
	private final Filter<GameObject> TIN_FILTER = Rocks.TIN.filter(ctx);
	private final Filter<GameObject> COPPER_FILTER = Rocks.COPPER.filter(ctx);

	public MiningInstructor(IClientContext ctx) {
		super(ctx, "Mining Instructor");
	}

	private static final int[] FURNACE_BOUND = {-440, -40, -220, 0, -200, 32};

	@Override
	public void run() {
		if (tryContinue()) return;

		if (!npc().valid()) {
			enter();
			if (!npc().valid()) {
				GameObject rock = ctx.objects.select().select(ObjectDefinition.name(ctx, "Rocks")).nearest().limit(6).shuffle().poll();
				ctx.movement.step(rock.tile().derive(Random.nextInt(-2, 3), Random.nextInt(-2, 3)));
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return npc().valid();
					}
				}, 300, 12);
			}
			return;
		}

		switch (stage()) {
			case 8:
				if (ctx.chat.visible("To prospect a mineable rock")) {
					prospect(TIN_FILTER);
					return;
				}
				if (ctx.chat.visible("So now you know there's tin in the grey rocks")) {
					prospect(COPPER_FILTER);
					return;
				}

				if (mine()) return;
				break;
			case 9:
			default:
				if (!ctx.inventory.select().id(BRONZE_DAGGER).isEmpty()) {
					leave();
					return;
				}

				if (ctx.inventory.select().id(BRONZE_BAR).isEmpty()) {
					if (mine()) return;

					ctx.game.tab(Game.Tab.INVENTORY);

					final GameObject furnace = ctx.objects.select().select(ObjectDefinition.name(ctx, "Furnace")).each(Interactive.doSetBounds(FURNACE_BOUND)).nearest().poll();

					if (ctx.camera.prepare(furnace) && ctx.inventory.select(TIN_ORE, COPPER_ORE)) {
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.inventory.selectedItemIndex() > -1;
							}
						}, 200, 5)) {
							final Item item = ctx.inventory.selectedItem();
							if (item.valid() && furnace.interact("Use", item.name() + " -> Furnace")) {
								Condition.wait(new Callable<Boolean>() {
									@Override
									public Boolean call() throws Exception {
										return ctx.chat.queryContinue();
									}
								}, 200, 20);
							}
						}
					}
					return;
				}

				if (!ctx.inventory.select().id(HAMMER).isEmpty()) {
					if (ctx.chat.visible("What would you like to make?")) {
						final Component dagger = ctx.chat.getComponentByText("Dagger");
						if (dagger.valid()) {
							final Component parent = dagger.parent();
							if (parent != null) {
								final Component[] components = parent.components();
								if (components.length > 0 && components[Random.nextInt(0, components.length)].click("Smith 1", "Bronze dagger")) {
									Condition.wait(new Callable<Boolean>() {
										@Override
										public Boolean call() throws Exception {
											return !ctx.inventory.select().id(BRONZE_DAGGER).isEmpty();
										}
									}, 200, 10);
								}
								return;
							}
						}
					}

					ctx.game.tab(Game.Tab.INVENTORY);

					final GameObject anvil = ctx.objects.select().select(ObjectDefinition.name(ctx, "Anvil")).nearest().limit(2).shuffle().poll();
					if (ctx.camera.prepare(anvil)) {
						if (ctx.inventory.select(BRONZE_BAR)) {
							final Item bar = ctx.inventory.select().id(BRONZE_BAR).poll();
							if (anvil.interact("Use", bar.name() + " -> Anvil")) {
								Condition.wait(new Callable<Boolean>() {
									@Override
									public Boolean call() throws Exception {
										return ctx.chat.visible("What would you like to make?");
									}
								}, 200, 20);
							}
						}
					}
					return;
				}

				break;
		}

		super.run();
	}

	private boolean mine() {
		if (!ctx.inventory.select().id(BRONZE_PICKAXE).isEmpty()) {
			if (ctx.inventory.select().id(TIN_ORE).isEmpty()) {
				mine(TIN_FILTER);
				return true;
			}
			if (ctx.inventory.select().id(COPPER_ORE).isEmpty()) {
				mine(COPPER_FILTER);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void enter() {
		QuestGuide.leave(ctx);
	}

	@Override
	protected void leave() {
		final GameObject gate = ctx.objects.select().select(ObjectDefinition.name(ctx, "Gate")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_CHAIN_GATE_EW)).nearest().poll();
		if (ctx.camera.prepare(gate) && gate.interact("Open")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.chat.visible("In this area you will find out about combat with swords");
				}
			});
		}
	}

	private void mine(Filter<GameObject> filter) {
		final GameObject rock = rock(filter);
		if (ctx.camera.prepare(rock) && rock.interact("Mine", "Rocks")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !rock.valid();
				}
			}, 250, 16);
		}
	}

	private GameObject rock(Filter<GameObject> filter) {
		return ctx.objects.select().select(ObjectDefinition.name(ctx, "Rocks")).select(filter).nearest().limit(4).shuffle().poll();
	}

	private void prospect(Filter<GameObject> filter) {
		final GameObject rock = rock(filter);
		if (ctx.camera.prepare(rock) && rock.interact("Prospect", "Rocks")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.chat.visible("This rock contains ");
				}
			}, 250, 16);
		}
	}
}
