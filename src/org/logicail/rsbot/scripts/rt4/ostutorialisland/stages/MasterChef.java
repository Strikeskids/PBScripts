package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.cache.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.framework.util.LoopCondition;
import org.logicail.rsbot.scripts.rt4.ostutorialisland.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Interactive;

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

		switch (stage()) {
			case 4:
				if (!npc().valid()) {
					enter();
					return;
				}

				if (!ctx.inventory.select().id(POT_OF_FLOUR).isEmpty() && !ctx.inventory.select().id(BUCKET_OF_WATER).isEmpty()) {
					ctx.inventory.itemOnItem(POT_OF_FLOUR, BUCKET_OF_WATER);
					return;
				}

				if (!ctx.inventory.select().id(DOUGH).isEmpty()) {
					final GameObject range = ctx.objects.select().select(ObjectDefinition.name(ctx, "Range")).each(Interactive.doSetBounds(BOUNDS_RANGE)).nearest().poll();
					if (Random.nextBoolean() && ctx.camera.prepare(range) && ctx.inventory.select(DOUGH) || (ctx.inventory.select(DOUGH) && ctx.camera.prepare(range))) {
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
				break;
			case 5:
				if (ctx.chat.visible("Once you've examined this menu use the next door") || ctx.chat.visible("Range cooking.") && ctx.chat.visible("Music.")) {
					leave();
				}
				break;
			default:
				leave();
				return;
		}

		super.run();
	}

	@Override
	protected void enter() {
		if (!ctx.players.local().inMotion() || ctx.movement.distance(ctx.movement.destination(), ctx.players.local()) < Random.nextInt(2, 5)) {
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(new int[]{0, 32, -240, 0, 0, 120})).nearest().poll();
			if (door.valid() && ctx.camera.prepare(door) && !npc().valid() && door.interact("Open", "Door")) {
				LoopCondition.wait(new LoopCondition(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return npc().valid();
					}
				}, new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().inMotion();
					}
				}), 200, 10);
				Condition.sleep(200);
			}
		} else {
			Condition.sleep(400);
		}
	}

	@Override
	protected void leave() {
		// Find north most door
		final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).within(15).sort(new Comparator<GameObject>() {
			@Override
			public int compare(GameObject o1, GameObject o2) {
				return Integer.valueOf(o2.tile().y()).compareTo(o1.tile().y());
			}
		}).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_EW)).poll();
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
	}

	@Override
	public boolean valid() {
		return super.valid() || stage() == 4;
	}
}
