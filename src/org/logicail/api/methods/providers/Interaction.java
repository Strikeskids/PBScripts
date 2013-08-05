package org.logicail.api.methods.providers;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:38
 */
public class Interaction extends LogicailMethodProvider {
	public Interaction(LogicailMethodContext ctx) {
		super(ctx);
	}

	public boolean prepare(Interactive target) {
		if (!(target instanceof Locatable)) {
			throw new IllegalArgumentException("not an instace of Interactive");
		}

		Locatable locatable = (Locatable) target;
		if (!target.isValid()) {
			return false;
		}

		if (target.isOnScreen()) {
			return true;
		}

		ctx.camera.turnTo(locatable);
		sleep(200, 1000);

		if (target.isOnScreen()) {
			return true;
		}

		if (ctx.movement.getDistance(locatable) >= 6) {
			final Tile destination = ctx.movement.reachableNear(locatable);
			if (destination != Tile.NIL && ctx.movement.findPath(destination).traverse()) {
				ctx.waiting.wait(10000, new Condition() {
					@Override
					public boolean validate() {
						return ctx.movement.getDistance(destination) < 6;
					}
				});
			}
			if (target.isOnScreen()) {
				return true;
			} else {
				ctx.camera.turnTo(locatable);
				sleep(200, 1000);
				return target.isOnScreen();
			}
		}

		return false;

	}

	public boolean interact(Interactive target, String action) {
		return interact(target, action, null);
	}

	public boolean interact(Interactive target, String action, String option) {
		if (!target.isValid() || !prepare(target)) {
			return false;
		}

		if (ctx.menu.isOpen() && ctx.menu.close()) {
			// Close the menu since Timer won't fix this
			sleep(111, 555);
		}

		switch (Random.nextInt(0, 8)) {
			case 0:
				String name = null;
				if (target.hover()) {
					if (target instanceof Npc) {
						name = ((Npc) target).getName();
					} else if (target instanceof GameObject) {
						name = ((GameObject) target).getName();
					}
				}
				if (target.interact("Examine", name)) {
					sleep(111, 2222);
				}
				break;
			case 1:
				if (target.hover() && ctx.menu.indexOf("Walk here") > -1) {
					sleep(111, 555);
					if (ctx.menu.click("Walk here")) {
						sleep(100, 1900);
					}
				}
				break;
			case 2:
				if (target instanceof Npc && target.hover() && ctx.menu.indexOf("Talk to") > -1) {
					sleep(200, 800);
					if (ctx.menu.click("Talk to")) {
						sleep(100, 1900);
					}
				}
				break;
		}

		return target.interact(action, option);
	}
}
