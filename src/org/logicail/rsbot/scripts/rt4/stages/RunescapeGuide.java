package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.OSTutorialIsland;
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

	public RunescapeGuide(IClientContext ctx) {
		super(ctx, RUNESCAPE_GUIDE);
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		if (ctx.chat.visible("To continue the tutorial go through that door", "try it with the things in this room, then click on the door", "Follow the path to find the next instructor.")) {
			final GameObject door = ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_E)).nearest().poll();
			if (ctx.camera.prepare(door) && door.interact("Open", "Door")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !valid();
					}
				}, 200, 25);
			}
			return;
		}

		super.run();
	}
}
