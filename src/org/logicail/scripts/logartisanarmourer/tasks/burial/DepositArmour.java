package org.logicail.scripts.logartisanarmourer.tasks.burial;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.api.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:24
 */
public class DepositArmour extends Node {
	public static final int ID_CHUTE = 29396;
	private final int ingotId;

	public DepositArmour(MyMethodContext ctx, int ingotId) {
		super(ctx);
		this.ingotId = ingotId;
	}

	@Override
	public void execute() {
		LogArtisanArmourer.instance.options.isSmithing = false;

		if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
			return;
		}

		for (GameObject chute : ctx.objects.select().id(ID_CHUTE).nearest().first()) {
			if (ctx.camera.turnTo(chute)) {
				if (chute.interact("Deposit-armour", "Chute")) {
					ctx.waiting.wait(5000, new Condition() {
						@Override
						public boolean validate() {
							return ctx.backpack.select().id(LogArtisanArmourer.ARMOUR_ID_LIST).isEmpty();
						}
					});
					sleep(150, 750);
				}
			} else {
				Tile tile = chute.getLocation().randomize(2, 2);
				if (tile != Tile.NIL && ctx.movement.stepTowards(tile)) {
					sleep(500, 1500);
				}
			}
		}
	}

	@Override
	public boolean activate() {
		return ctx.backpack.select().id(ingotId).isEmpty()
				&& !ctx.backpack.select().id(LogArtisanArmourer.ARMOUR_ID_LIST).isEmpty();
	}
}
