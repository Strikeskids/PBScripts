package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.logicail.api.methods.QueryHelper;
import org.logicail.api.providers.Condition;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.tasks.swords.MakeSword;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 21:02
 */
public class Anvil extends LogicailMethodProvider {
	private static final int[] ID_ANVIL = {4046};
	private static final int[] ID_ANVIL_SWORD = {4047, 24664, 24677, 24678, 15520, 20258};
	private static final int[] ID_ANVIL_TRACK = {24820};
	final Area area = StayInArea.area;
	//private LogicailMethodContext ctx;
	private LogArtisanArmourerOptions options;
	private int[] ids;
	private GameObject target = null;

	public Anvil(LogicailMethodContext ctx) {
		super(ctx);
		options = ((LogArtisanArmourer) ctx.script).options;
		ids = getAnvilId();
	}

	public void click() {
		if (target == null) {
			target = QueryHelper.first(ctx.objects.select().id(ids).select(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					return area.contains(gameObject);
				}
			}).shuffle());
		}

		if (target != null) {
			if (ctx.skillingInterface.isOpen() && !ctx.skillingInterface.close()) {
				return;
			}

			if (ctx.skillingInterface.isProductionInterfaceOpen() && !ctx.skillingInterface.cancelProduction()) {
				return;
			}

			if (ctx.interaction.interact(target, "Smith", "Anvil")) {
				ctx.waiting.wait(600, new Condition() {
					@Override
					public boolean validate() {
						return ctx.skillingInterface.isOpen();
					}
				});
				if (ctx.players.local().getAnimation() != -1 && !ctx.skillingInterface.isOpen() && target.hover() && !ctx.skillingInterface.isOpen()) {
					ctx.menu.click("Smith", "Anvil");
				}

				final Timer t = new Timer(Random.nextInt(5000, 7000));
				while (t.isRunning()) {
					if (ctx.skillingInterface.isOpen() || ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, MakeSword.WIDGET_SWORD_COOLDOWN).isValid()) {
						target = null;
						sleep(200, 800);
						break;
					}
					if (ctx.skillingInterface.isProductionInterfaceOpen()) {
						ctx.skillingInterface.cancelProduction();
					}
					sleep(100, 300);
				}
				sleep(400, 1500);
			}
		}
	}

	private int[] getAnvilId() {
		switch (options.mode) {
			case BURIAL_ARMOUR:
				return ID_ANVIL;
			case CEREMONIAL_SWORDS:
				return ID_ANVIL_SWORD;
			case REPAIR_TRACK:
				return ID_ANVIL_TRACK;
		}
		return ID_ANVIL_TRACK;
	}
}
