package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.powerbot.script.lang.ChainingIterator;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 21:23
 */
public class DoorOpener extends IMethodProvider implements ChainingIterator<GameObject> {
	public DoorOpener(IMethodContext context) {
		super(context);
	}

	public static boolean open(IMethodContext ctx, final GameObject door) {
		if (openDoor(ctx, door)) return true;

		if (door.isValid()) {
			if (ctx.camera.getPitch() > 50) {
				ctx.camera.setPitch(Random.nextInt(0, 40));
			} else {
				ctx.camera.setAngle(Random.nextInt(0, 360));
			}
			if (openDoor(ctx, door)) return true;
		}

		return !door.isValid();
	}

	private static boolean openDoor(IMethodContext ctx, final GameObject door) {
		if (ctx.camera.prepare(door)) {
			if (door.interact("Open")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !door.isValid();
					}
				})) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean next(int i, final GameObject door) {
		return open(ctx, door);
	}
}
