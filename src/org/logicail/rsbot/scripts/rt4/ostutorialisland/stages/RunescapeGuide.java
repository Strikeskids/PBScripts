package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import com.logicail.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.logicail.rsbot.scripts.rt4.ostutorialisland.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Interactive;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 12:24
 */
public class RunescapeGuide extends Talker {
	public static final String RUNESCAPE_GUIDE = "RuneScape Guide";

	public RunescapeGuide(RT4ClientContext ctx) {
		super(ctx, RUNESCAPE_GUIDE);
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		if (stage() >= 2 || ctx.chat.visible("You can interact with many items of scenery by simply clicking")) {
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
		final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_E)).nearest().poll();
		if (ctx.camera.prepare(door) && door.interact("Open", "Door")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !valid();
				}
			}, 200, 25);
		}
	}
}
