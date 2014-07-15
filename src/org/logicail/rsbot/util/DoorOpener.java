package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 21:23
 */
public class DoorOpener extends IClientAccessor {
	public DoorOpener(IClientContext context) {
		super(context);
	}

	public static boolean open(IClientContext ctx, final GameObject door) {
		if (openDoor(ctx, door)) return true;

		if (door.valid()) {
			if (ctx.camera.pitch() > 50) {
				ctx.camera.pitch(Random.nextInt(0, 40));
			} else {
				ctx.camera.angle(Random.nextInt(0, 360));
			}
			if (openDoor(ctx, door)) return true;
		}

		return !door.valid();
	}

	public static int[] DOOR_BOUNDS_NS = {-256, 256, -900, 0, 128, 256};
	public static int[] DOOR_BOUNDS_EW = {128, 256, -900, 0, -256, 256};

	private static boolean openDoor(IClientContext ctx, final GameObject door) {
		if (ctx.camera.prepare(door)) {
			if (door.interact("Open")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !door.valid();
					}
				}, 100, 20)) {
					return true;
				}
			}
		}
		return false;
	}
}
