package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.Item;

import java.util.Comparator;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 17:23
 */
public class MasterChef extends Talker {
	public MasterChef(IClientContext ctx) {
		super(ctx, "Master Chef");
	}

	private static final int[] BOUNDS_RANGE = {-100, 100, -64, 0, -32, 32};
	private static int POT_OF_FLOUR = 2516;
	private static int BUCKET_OF_WATER = 1929;
	private static int DOUGH = 2307;

	@Override
	public void run() {
		if (tryContinue()) return;

		if (ctx.chat.visible("This is the base for many of the meals. To make dough")) {
			if (ctx.inventory.select(POT_OF_FLOUR)) {
				Condition.sleep(333);
				if (ctx.inventory.selected()) {
					final Item water = ctx.inventory.select().id(BUCKET_OF_WATER).shuffle().poll();
					if (water.click("Use", "Pot of flour -> Bucket of water")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !water.valid();
							}
						}, 200, 6);
					}
				}
			}
			return;
		}

		ctx.inventory.deselect();

		if (ctx.chat.visible("Follow the path until you get to the door with the yellow arrow")) {
			if (!ctx.players.local().inMotion() || ctx.movement.distance(ctx.movement.destination(), ctx.players.local()) < Random.nextInt(2, 5)) {
				final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(new int[]{0, 32, -240, 0, 0, 120})).nearest().poll();
				if (door.valid() && ctx.camera.prepare(door) && door.interact("Open", "Door")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.players.local().inMotion();
						}
					}, 50, 5);
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.movement.distance(ctx.movement.destination(), ctx.players.local()) < Random.nextInt(2, 5) || ctx.chat.visible("Talk to the chef indicated.");
						}
					}, 200, 20);
				}
			} else {
				Condition.sleep(400);
			}
			return;
		}

		if (ctx.chat.visible("Now you have made dough, you can cook it.")) {
			final GameObject range = ctx.objects.select().select(ObjectDefinition.name(ctx, "Range")).each(Interactive.doSetBounds(BOUNDS_RANGE)).nearest().poll();

			if (ctx.camera.prepare(range) && ctx.inventory.select(DOUGH)) {
				Condition.sleep(333);
				if (ctx.inventory.selected() && range.interact("Use", "Bread dough -> Range")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.chat.visible("Well done!");
						}
					}, 200, 20);
				}
			}
			return;
		}

		if (ctx.chat.visible("Once you've examined this menu use the next door") || ctx.chat.visible("Range cooking.") && ctx.chat.visible("Music.")) {
			// Find north most door
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).within(15).sort(new Comparator<GameObject>() {
				@Override
				public int compare(GameObject o1, GameObject o2) {
					return Integer.valueOf(o2.tile().y()).compareTo(o1.tile().y());
				}
			}).poll();
			door.bounds(OSTutorialIsland.BOUNDS_DOOR_EW);
			if (ctx.camera.prepare(door) && door.click("Open")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Now, how about showing some feelings?");
					}
				}, 200, 40)) {
					Condition.sleep(500);
				}
			}
			return;
		}

		super.run();
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible("Follow the path until you get to the door with the yellow arrow");
	}
}
