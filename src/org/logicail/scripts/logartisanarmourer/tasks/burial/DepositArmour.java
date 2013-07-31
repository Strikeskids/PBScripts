package org.logicail.scripts.logartisanarmourer.tasks.burial;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:24
 */
public class DepositArmour extends Node {
	public static final int ID_CHUTE = 29396;
	private final int ingotId;
	private final LogArtisanArmourerOptions options;

	public DepositArmour(LogicailMethodContext ctx) {
		super(ctx);
		this.options = ((LogArtisanArmourer) ctx.script).options;
		this.ingotId = options.getIngotID();
	}

	@Override
	public void execute() {
		options.isSmithing = false;

		if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
			return;
		}

		for (GameObject chute : ctx.objects.select().id(ID_CHUTE).nearest().first()) {
			if (ctx.interaction.interact(chute, "Deposit-armour", "Chute")) {
				ctx.waiting.wait(5000, new Condition() {
					@Override
					public boolean validate() {
						return ctx.backpack.select().id(LogArtisanArmourer.ARMOUR_ID_LIST).isEmpty();
					}
				});
				sleep(150, 750);
			}
		}
	}

	@Override
	public boolean activate() {
		return ctx.backpack.select().id(ingotId).isEmpty()
				&& !ctx.backpack.select().id(LogArtisanArmourer.ARMOUR_ID_LIST).isEmpty();
	}
}
