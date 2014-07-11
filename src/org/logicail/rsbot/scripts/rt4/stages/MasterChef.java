package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Game;
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

		if (ctx.chat.visible("Follow the path until you get to the door with the yellow arrow")) {
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(new int[]{0, 32, -240, 0, 0, 120})).nearest().poll();
			ctx.inventory.deselect();
			if (door.valid() && ctx.camera.prepare(door) && door.click("Open", "Door")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Talk to the chef indicated.");
					}
				}, 200, 50);
			}
			return;
		}

		if (ctx.chat.visible("This is the base for many of the meals. To make dough")) {
			final Item flour = ctx.inventory.select().id(POT_OF_FLOUR).shuffle().poll();
			if (ctx.inventory.selected() && ctx.inventory.selectedItem().id() != POT_OF_FLOUR) {
				ctx.inventory.deselect();
				return;
			}

			if (ctx.inventory.selectedItem().id() == POT_OF_FLOUR || flour.click("Use")) {
				Condition.sleep(333);
				if (ctx.inventory.selected()) {
					final Item water = ctx.inventory.select().id(BUCKET_OF_WATER).shuffle().poll();
					if (water.click("Use", "Pot of flour -> Bucket of water")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !flour.valid();
							}
						}, 200, 6);
					}
				}
			}
			return;
		}

		if (ctx.chat.visible("Now you have made dough, you can cook it.")) {
			final GameObject range = ctx.objects.select().select(ObjectDefinition.name(ctx, "Range")).each(Interactive.doSetBounds(BOUNDS_RANGE)).nearest().poll();

			if (ctx.inventory.selected() && ctx.inventory.selectedItem().id() != DOUGH) {
				ctx.inventory.selectedItem().interact("Cancel");
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.inventory.selected();
					}
				}, 200, 5);
				return;
			}

			final Item dough = ctx.inventory.select().id(DOUGH).poll();
			if (dough.valid() && ctx.camera.prepare(range) && dough.click("Use")) {
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

		if (ctx.chat.visible("Once you've examined this menu use the next door")) {
			// Find north most door
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).within(15).sort(new Comparator<GameObject>() {
				@Override
				public int compare(GameObject o1, GameObject o2) {
					return Integer.valueOf(o2.tile().y()).compareTo(o1.tile().y());
				}
			}).poll();
			door.bounds(OSTutorialIsland.BOUNDS_DOOR_EW);
			if (ctx.camera.prepare(door) && door.click("Open")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Now, how about showing some feelings?");
					}
				}, 200, 40);
			}
			return;
		}

		if (ctx.chat.visible("Click on the flashing icon")) {
			ctx.game.tab(Game.Tab.MUSIC);
			return;
		}

		super.run();
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible("Follow the path until you get to the door with the yellow arrow");
	}
}
