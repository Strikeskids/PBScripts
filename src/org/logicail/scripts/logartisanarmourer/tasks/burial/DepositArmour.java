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
	public static final int[] ARMOUR_ID_LIST = {20572, 20573, 20574, 20575,
			20576, 20577, 20578, 20579, 20580, 20581, 20582, 20583, 20584,
			20585, 20586, 20587, 20588, 20589, 20590, 20591, 20592, 20593,
			20594, 20595, 20596, 20597, 20598, 20599, 20600, 20601, 20602,
			20603, 20604, 20605, 20606, 20607, 20608, 20609, 20610, 20611,
			20612, 20613, 20614, 20615, 20616, 20617, 20618, 20619, 20620,
			20621, 20622, 20623, 20624, 20625, 20626, 20627, 20628, 20629,
			20630, 20631};
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
						return !activate();
					}
				});
				sleep(150, 750);
			}
		}
	}

	@Override
	public boolean activate() {
		return ctx.backpack.select().id(ingotId).isEmpty()
				&& !ctx.backpack.select().id(ARMOUR_ID_LIST).isEmpty();
	}
}
