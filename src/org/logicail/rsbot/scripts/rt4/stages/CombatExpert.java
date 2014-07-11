package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 20:19
 */
public class CombatExpert extends Talker {
	private static final String LEFT_CLICK_YOUR_DAGGER_TO = "Left click your dagger to";
	private static final String TO_ATTACK_THE_RAT_RIGHT_CLICK_IT = "To attack the rat, right click it";
	private static final String WHILE_YOU_ARE_FIGHTING_YOU_WILL_SEE_A_BAR = "While you are fighting you will see a bar";
	private static final String PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT = "Pass through the gate and talk to the combat";
	public static final String YOU_HAVE_COMPLETED_THE_TASKS_HERE = "You have completed the tasks here.";
	private static final int BRONZE_DAGGER_ID = 1205;

	public CombatExpert(IClientContext ctx) {
		super(ctx, "Combat Instructor");
	}

	private HashSet<Tile> unreachable = new HashSet<Tile>();

	@Override
	public void run() {
		if (tryContinue()) return;

		if (ctx.chat.visible("icon of a man, the one to the right of your backpack")) {
			ctx.game.tab(Game.Tab.EQUIPMENT);
			return;
		}

		if (ctx.chat.visible("From here you can see what items you have equipped")) {
			final Component stats = ctx.widgets.widget(387).component(17);
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

		if (ctx.chat.visible("In your worn inventory panel, right click on the dagger")) {
			if (ctx.game.tab() != Game.Tab.INVENTORY) {
				ctx.game.tab(Game.Tab.INVENTORY);
				return;
			}
			for (final Item item : ctx.inventory.select().name("Bronze sword", "Wooden shield").shuffle()) {
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

		if (ctx.chat.visible("Click on the flashing crossed swords")) {
			ctx.game.tab(Game.Tab.ATTACK);
			return;
		}

		if (ctx.chat.visible("From this interface you can select the type of attack")) {
			traverseCage();
			return;
		}

		if (ctx.chat.visible(TO_ATTACK_THE_RAT_RIGHT_CLICK_IT)) {
			final Npc rat = ctx.npcs.select().name("Giant rat").select(new Filter<Npc>() {
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

		if (ctx.chat.visible(WHILE_YOU_ARE_FIGHTING_YOU_WILL_SEE_A_BAR)) {
			Condition.sleep(333);
			return;
		}

		if (ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT) && !npc().valid()) {
			traverseCage();
			return;
		}

		if (!ctx.inventory.select().id(841, 882).isEmpty()) {
			if (ctx.game.tab() != Game.Tab.INVENTORY) {
				ctx.game.tab(Game.Tab.INVENTORY);
				return;
			}
			for (final Item item : ctx.inventory.select().id(841, 882).shuffle()) {
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

		if (ctx.chat.visible("try killing another rat")) {

			Npc rat = ctx.npcs.select().name("Giant rat").select(new Filter<Npc>() {
				@Override
				public boolean accept(Npc npc) {
					return npc.interacting().equals(ctx.players.local());
				}
			}).nearest().poll();

			if (!rat.valid()) {
				rat = ctx.npcs.select().name("Giant rat").select(new Filter<Npc>() {
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
		if (ctx.chat.visible(YOU_HAVE_COMPLETED_THE_TASKS_HERE)) {
			final GameObject ladder = ctx.objects.select().select(ObjectDefinition.name(ctx, "Ladder")).nearest().poll();
			if (ctx.camera.prepare(ladder) && ladder.click("Climb-up")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Banking.");
					}
				}, 200, 40);
			}
			return;
		}

		super.run();
	}

	private void traverseCage() {
		final GameObject gate = ctx.objects.select().select(ObjectDefinition.name(ctx, "Gate")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_CHAIN_GATE_NS)).nearest(ctx.players.local()).poll();
		if (ctx.camera.prepare(gate) && gate.click("Open")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					if (ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT)) {
						return npc().valid();
					}
					if (ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT)) {
						return ctx.npcs.select().name("Giant rat").nearest().poll().valid();
					}
					return false;
				}
			}, 200, 20);
		}
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible(PASS_THROUGH_THE_GATE_AND_TALK_TO_THE_COMBAT, TO_ATTACK_THE_RAT_RIGHT_CLICK_IT);
	}
}
