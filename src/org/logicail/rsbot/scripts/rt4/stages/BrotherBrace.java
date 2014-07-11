package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
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

	private static final String CLICK_ON_THE_FLASHING_ICON_TO_OPEN_THE_PRAYER = "Click on the flashing icon to open the Prayer";
	private static final String SMILING_FACE_TO_OPEN_YOUR_FRIENDS_LIST = "smiling face to open your friends list";
	private static final String ON_THE_OTHER_FLASHING_FACE_TO_THE_RIGHT = "on the other flashing face to the right";

	public BrotherBrace(IClientContext ctx) {
		super(ctx, "Brother Brace");
	}

	public static int[] LARGE_DOOR_BOUNDS = {0, 32, -240, 0, 6, 140};
	private static int[] SUPPORT_MODELS = {2212, 2213, 2176, 2177, 2178};

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		if (ctx.chat.visible("You're almost finished on tutorial island.", "Nope, I'm ready to move on!", "Just follow the path to the Wizard's house")) {
			final GameObject altar = ctx.objects.select().select(ObjectDefinition.name(ctx, "Altar")).nearest().poll();
			if (altar.valid()) {
				final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_S)).nearest(altar).poll();
				if (ctx.camera.prepare(door) && door.click("Open")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.chat.visible("Just follow the path to the Wizard's house");
						}
					}, 200, 20);
				}
			}
			return;
		}
		if (!npc().tile().matrix(ctx).reachable()) {
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
				log.info("Open door");
				if (ctx.camera.prepare(door) && door.click("Open")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return npc().tile().matrix(ctx).reachable();
						}
					}, 200, 20);
				}
			}
			return;
		}
		if (ctx.chat.visible(CLICK_ON_THE_FLASHING_ICON_TO_OPEN_THE_PRAYER)) {
			ctx.game.tab(Game.Tab.PRAYER);
			return;
		}
		if (ctx.chat.visible(SMILING_FACE_TO_OPEN_YOUR_FRIENDS_LIST)) {
			ctx.game.tab(Game.Tab.FRIENDS_LIST);
			return;
		}
		if (ctx.chat.visible(ON_THE_OTHER_FLASHING_FACE_TO_THE_RIGHT)) {
			//ctx.game.tab(Game.Tab.IGNORED_LIST);
			final Component component = findTextureId(905);
			if (component != null && component.click("Ignore List")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return ctx.game.tab() == Game.Tab.IGNORED_LIST;
					}
				}, 50, 10);
			}
			return;
		}

		super.run();
	}

	private Component findTextureId(final int texture) {
		for (Component component : this.ctx.widgets.widget(548).components()) {
			if (component.textureId() == texture) {
				return component;
			}
		}

		return null;
	}

	@Override
	public boolean valid() {
		return super.valid() || ctx.chat.visible("Follow the path to the chapel", CLICK_ON_THE_FLASHING_ICON_TO_OPEN_THE_PRAYER, SMILING_FACE_TO_OPEN_YOUR_FRIENDS_LIST, ON_THE_OTHER_FLASHING_FACE_TO_THE_RIGHT);
	}
}
