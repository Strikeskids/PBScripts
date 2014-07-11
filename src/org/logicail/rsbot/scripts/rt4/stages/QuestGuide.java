package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.ActionManifest;
import org.logicail.rsbot.scripts.rt4.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Interactive;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 18:06
 */
@ActionManifest(name = "Quest Guide")
public class QuestGuide extends Talker {
	private static final String NOW_THAT_YOU_HAVE_THE_RUN_BUTTON_TURNED_ON = "Now that you have the run button turned on";

	public QuestGuide(IClientContext ctx) {
		super(ctx, "Quest Guide");
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		if (!npc().valid()) {
			final GameObject armour = ctx.objects.select().select(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					return gameObject.type() == GameObject.Type.INTERACTIVE;
				}
			}).select(ObjectDefinition.name(ctx, "Suit of armour")).poll();

			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).nearest(armour).poll();
			if (door.tile().distanceTo(ctx.players.local()) > 10) {
				final Tile tile = armour.tile().derive(0, 7).derive(Random.nextInt(-2, 5), Random.nextInt(-2, 5));
				final double distanceTo = tile.distanceTo(ctx.players.local());
				if (distanceTo > 4) {
					ctx.movement.myWalk(tile);
					return;
				}
				return;
			}
		}

		if (ctx.chat.getComponentByText(NOW_THAT_YOU_HAVE_THE_RUN_BUTTON_TURNED_ON).valid()) {
			final GameObject armour = ctx.objects.select().select(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					return gameObject.type() == GameObject.Type.INTERACTIVE;
				}
			}).select(ObjectDefinition.name(ctx, "Suit of armour")).poll();
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_N)).nearest(armour).poll();
			if (ctx.camera.prepare(door) && door.click("Open")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return npc().valid();
					}
				}, 200, 50);
			}
			return;
		}

		if (ctx.chat.visible("Open the Quest Journal.")) {
			ctx.game.tab(Game.Tab.QUESTS);
			return;
		}

		if (ctx.chat.visible("It's time to enter some caves")) {
			final GameObject ladder = ctx.objects.select().select(ObjectDefinition.name(ctx, "Ladder")).nearest().poll();
			if (ctx.camera.prepare(ladder)) {
				if (ladder.click("Climb-down")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.chat.visible("Next let's get you a weapon");
						}
					}, 200, 10);
				}
			}
			return;
		}

		super.run();
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.getComponentByText(NOW_THAT_YOU_HAVE_THE_RUN_BUTTON_TURNED_ON).valid();
	}
}
