package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import com.logicail.loader.rt4.wrappers.NpcDefinition;
import com.logicail.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.ostutorialisland.OSTutorialIsland;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 20:19
 */
public class CombatExpert extends Talker {
	public static final String YOU_HAVE_COMPLETED_THE_TASKS_HERE = "You have completed the tasks here.";
	private static final String LEFT_CLICK_YOUR_DAGGER_TO = "Left click your dagger to";
	private static final String TO_ATTACK_THE_RAT_RIGHT_CLICK_IT = "To attack the rat, right click it";
	private static final String WHILE_YOU_ARE_FIGHTING_YOU_WILL_SEE_A_BAR = "While you are fighting you will see a bar";
	private static final String PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT = "Pass through the gate and talk to the combat";
	private static final int BRONZE_DAGGER_ID = 1205;
	private static final int BRONZE_SWORD = 1277;
	private static final int WOODEN_SHIELD = 1171;
	private static final int[] BOUNDS_GIANT_RAT = {-48, 48, -68, 0, -48, 48};
	private static final String FROM_THIS_INTERFACE_YOU_CAN_SELECT_THE_TYPE_OF_ATTACK = "From this interface you can select the type of attack";
	private static final String FROM_HERE_YOU_CAN_SEE_WHAT_ITEMS_YOU_HAVE_EQUIPPED = "From here you can see what items you have equipped";
	private static final String IN_YOUR_WORN_INVENTORY_PANEL_RIGHT_CLICK_ON_THE_DAGGER = "In your worn inventory panel, right click on the dagger";

	public static final String[] VALID_STRINGS = {
			LEFT_CLICK_YOUR_DAGGER_TO,
			TO_ATTACK_THE_RAT_RIGHT_CLICK_IT,
			WHILE_YOU_ARE_FIGHTING_YOU_WILL_SEE_A_BAR,
			PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT,
			FROM_THIS_INTERFACE_YOU_CAN_SELECT_THE_TYPE_OF_ATTACK,
			FROM_HERE_YOU_CAN_SEE_WHAT_ITEMS_YOU_HAVE_EQUIPPED,
			IN_YOUR_WORN_INVENTORY_PANEL_RIGHT_CLICK_ON_THE_DAGGER
	};

	private static final String TRY_KILLING_ANOTHER_RAT = "try killing another rat";

	private HashSet<Tile> unreachable = new HashSet<Tile>();

	public CombatExpert(IClientContext ctx) {
		super(ctx, "Combat Instructor");
	}

	private BasicQuery<Npc> rat() {
		return ctx.npcs.select().select(NpcDefinition.name(ctx, "Giant rat")).each(Interactive.doSetBounds(BOUNDS_GIANT_RAT));
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		switch (stage()) {
			case 10:
				if (ctx.chat.visible(FROM_HERE_YOU_CAN_SEE_WHAT_ITEMS_YOU_HAVE_EQUIPPED)) {
					final Component stats = ctx.widgets.widget(387).component(17);
					if (stats.valid()) {
						if (stats.click("View equipment stats")) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ctx.chat.visible(LEFT_CLICK_YOUR_DAGGER_TO);
								}
							}, 200, 5);
						}
						return;
					}
				}

				final Component close = ctx.widgets.widget(84).component(3);
				if (close.valid()) {
					close.click("Close");
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !close.valid();
						}
					}, 200, 5);
					return;
				}

				if (ctx.chat.visible(LEFT_CLICK_YOUR_DAGGER_TO)) {
					if (ctx.game.tab() != Game.Tab.INVENTORY) {
						ctx.game.tab(Game.Tab.INVENTORY);
						return;
					}

					for (final Item item : ctx.inventory.select().id(BRONZE_DAGGER_ID).first()) {
						item.interact("Wield", "Bronze dagger");
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !item.valid();
							}
						}, 200, 5);
					}
					return;
				}

				ctx.inventory.deselect();


				if (!ctx.inventory.select().id(BRONZE_SWORD, WOODEN_SHIELD).isEmpty()) {
					if (ctx.game.tab() != Game.Tab.INVENTORY) {
						ctx.game.tab(Game.Tab.INVENTORY);
						return;
					}
					for (final Item item : ctx.inventory.shuffle()) {
						item.click("Wield");
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !item.valid();
							}
						}, 200, 5);
					}
					return;
				}

				break;
			case 11:
				if (ctx.chat.visible(WHILE_YOU_ARE_FIGHTING_YOU_WILL_SEE_A_BAR)) {
					Condition.sleep(333);
					return;
				}

				if (ctx.chat.visible(FROM_THIS_INTERFACE_YOU_CAN_SELECT_THE_TYPE_OF_ATTACK) || ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT) && !npc().valid()) {
					traverseCage();
					return;
				}

				if (ctx.chat.visible(TO_ATTACK_THE_RAT_RIGHT_CLICK_IT)) {
					final Npc rat = rat().select(new Filter<Npc>() {
						@Override
						public boolean accept(Npc npc) {
							return !npc.inCombat();
						}
					}).nearest().limit(3).shuffle().poll();

					if (ctx.camera.prepare(rat) && rat.interact("Attack", "Giant rat")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.chat.visible(WHILE_YOU_ARE_FIGHTING_YOU_WILL_SEE_A_BAR);
							}
						});
					}
					return;
				}
				break;
			case 12:
				if (!ctx.inventory.select().id(841, 882).isEmpty()) {
					if (ctx.game.tab() != Game.Tab.INVENTORY) {
						ctx.game.tab(Game.Tab.INVENTORY);
						return;
					}
					for (final Item item : ctx.inventory.shuffle()) {
						item.click("Wield");
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !item.valid();
							}
						}, 200, 5);
					}
					return;
				}


				if (ctx.chat.visible(TRY_KILLING_ANOTHER_RAT)) {
					Npc rat = rat().select(new Filter<Npc>() {
						@Override
						public boolean accept(Npc npc) {
							return npc.interacting().equals(ctx.players.local());
						}
					}).nearest().poll();

					if (!rat.valid()) {
						rat = rat().select(new Filter<Npc>() {
							@Override
							public boolean accept(Npc npc) {
								return !npc.inCombat() && !unreachable.contains(npc.tile());
							}
						}).nearest().limit(2).shuffle().poll();
					}

					final Tile tile = rat.tile();

					if (ctx.camera.prepare(rat) && rat.interact("Attack", "Giant rat")) {
						Condition.sleep(333);
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.chat.visible("I can't reach that!") && ctx.chat.queryContinue();
							}
						}, 200, 5)) {
							unreachable.add(tile);
							if (unreachable.size() > 5) {
								final GameObject gate = gate();
								if (gate.valid()) {
									LogicailArea area = new LogicailArea(gate.tile().derive(-4, -4), gate.tile().derive(5, 5));
									HashSet<Tile> reachable = new HashSet<Tile>();

									for (Tile dest : area.getTileArray()) {
										if (dest.matrix(ctx).reachable()) {
											reachable.add(dest);
										}
									}
									java.util.List<Tile> reachableShuffle = new ArrayList<Tile>(reachable);
									Collections.shuffle(reachableShuffle);
									for (Tile t : reachableShuffle) {
										ctx.movement.step(t);
										break;
									}
								}
							}
							return;
						}
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								final Actor actor = ctx.players.local().interacting();
								return actor == null || !actor.valid() || ctx.chat.visible(YOU_HAVE_COMPLETED_THE_TASKS_HERE);
							}
						}, 200, 50);
						Condition.sleep(333);
					}
					return;
				}

				leave();
				return;
		}

		if (ctx.chat.visible(YOU_HAVE_COMPLETED_THE_TASKS_HERE)) {
			return;
		}

		super.run();
	}

	@Override
	protected void enter() {

	}

	@Override
	protected void leave() {
		final GameObject ladder = ctx.objects.select().select(ObjectDefinition.name(ctx, "Ladder")).nearest().poll();
		if (ctx.camera.prepare(ladder) && ladder.interact("Climb-up")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.chat.visible("Banking.");
				}
			}, 200, 40);
		}
	}

	private void traverseCage() {
		final GameObject gate = gate();

		if (!ctx.definitions.get(gate).name.equals("Gate")) {
			return;
		}

		if (ctx.camera.prepare(gate) && gate.interact("Open")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					if (ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT)) {
						return npc().valid();
					}
					if (ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT)) {
						return rat().nearest().poll().valid();
					}
					return false;
				}
			}, 200, 20);
		}
	}

	private GameObject gate() {
		final GameObject wall = ctx.objects.select().select(ObjectDefinition.name(ctx, "Spear wall")).nearest().poll();
		return ctx.objects.select().select(ObjectDefinition.name(ctx, "Gate")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_CHAIN_GATE_NS)).nearest(wall).poll();
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT, TO_ATTACK_THE_RAT_RIGHT_CLICK_IT, FROM_THIS_INTERFACE_YOU_CAN_SELECT_THE_TYPE_OF_ATTACK);
	}
}
