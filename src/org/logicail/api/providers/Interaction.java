package org.logicail.api.providers;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.api.methods.MyMethodProvider;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Npc;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:38
 */
public class Interaction extends MyMethodProvider {
	public static boolean enabled = true;

	public Interaction(MyMethodContext ctx) {
		super(ctx);
	}

	public boolean interact(Interactive target, String action, String option) {
		if (!target.isValid() || !target.isOnScreen()) {
			return false;
		}

		if (enabled) {
			switch (Random.nextInt(0, 6)) {
				case 0:
					String name = null;
					if (target.hover()) {
						if (target instanceof Npc) {
							name = ((Npc) target).getName();
						} else if (target instanceof GameObject) {
							name = ((GameObject) target).getName();
						}
					}
					target.interact("Examine", name);
					sleep(200, 2200);
					break;
				case 1:
					target.interact("Walk here");
					sleep(200, 2200);
					break;
				case 2:
					if (target.hover()) {
						if (ctx.menu.indexOf("Talk to") > -1) {
							sleep(200, 800);
							if (ctx.menu.click("Talk to")) {
								sleep(200, 2200);
							}
						}
					}
					break;
			}
		}

		return target.interact(action, option);
	}
}
