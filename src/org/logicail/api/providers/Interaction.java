package org.logicail.api.providers;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:38
 */
public class Interaction<T extends Interactive & Locatable> extends LogicailMethodProvider {
	public Interaction(LogicailMethodContext ctx) {
		super(ctx);
	}

	public boolean interact(T target, String action) {
		return interact(target, action, null);
	}

	public boolean interact(T target, String action, String option) {
		if (!target.isValid()) {
			return false;
		}

		if (!ctx.camera.turnTo(target) || ctx.movement.getDistance(target) >= 12) {
			final Tile destination = ctx.movement.reachableNear(target);
			if (destination != Tile.NIL && ctx.movement.findPath(destination).traverse()) {
				ctx.waiting.wait(12000, new Condition() {
					@Override
					public boolean validate() {
						return ctx.movement.getDistance(destination) < 6;
					}
				});
			}
			if (!ctx.camera.turnTo(target)) {
				return false;
			}
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
