package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import com.logicail.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.logicail.rsbot.scripts.rt4.ostutorialisland.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Interactive;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 21:22
 */
public class BrotherBrace extends Talker {

	public BrotherBrace(RT4ClientContext ctx) {
		super(ctx, "Brother Brace");
	}

	public static int[] LARGE_DOOR_BOUNDS = {0, 32, -240, 0, 6, 140};
	private static int[] SUPPORT_MODELS = {2212, 2213, 2176, 2177, 2178};

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		if (stage() >= 17) {
			leave();
			return;
		}

		if (!npc().tile().matrix(ctx).reachable()) {
			enter();
			return;
		}

		super.run();
	}

	@Override
	protected void enter() {
		final GameObject support = ctx.objects.select().select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				final ObjectDefinition definition = ctx.definitions.get(gameObject);
				return definition != null && Arrays.equals(SUPPORT_MODELS, definition.modelIds);
			}
		}).nearest().poll();
		if (support.valid()) {
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Large door")).select(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					final ObjectDefinition definition = ctx.definitions.get(gameObject);
					if (definition != null) {
						for (String s : definition.actions) {
							if (s != null && s.equals("Open")) {
								return true;
							}
						}
					}
					return false;
				}
			}).each(Interactive.doSetBounds(LARGE_DOOR_BOUNDS)).nearest(support).limit(2).shuffle().poll();

			if (door.tile().distanceTo(ctx.players.local()) > 10) {
				ctx.movement.myWalk(door.tile().derive(Random.nextInt(-3, 3), Random.nextInt(-3, 3)));
				return;
			}

			if (ctx.camera.prepare(door) && door.interact("Open")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return npc().tile().matrix(ctx).reachable();
					}
				}, 200, 20);
			}
		}
	}

	@Override
	protected void leave() {
		final GameObject altar = ctx.objects.select().select(ObjectDefinition.name(ctx, "Altar")).nearest().poll();
		if (altar.valid()) {
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_S)).nearest(altar).poll();
			if (ctx.camera.prepare(door) && door.interact("Open")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Just follow the path to the Wizard's house");
					}
				}, 200, 20);
			}
		}
	}


	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible("Follow the path to the chapel") || stage() == 16;
	}
}
