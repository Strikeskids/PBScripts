package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.powerbot.script.lang.ChainingIterator;
import org.powerbot.script.util.Condition;
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

	@Override
	public boolean next(int i, final GameObject door) {
		return open(ctx, door);
	}

	public static boolean open(IMethodContext ctx, final GameObject door) {
		if (ctx.camera.prepare(door)) {
			if (door.interact("Open")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !door.isValid();
					}
				})) {
					ctx.log.info("Opened door");
				}
			}
		}

		return !door.isValid();
	}
}
