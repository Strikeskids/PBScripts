package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.tasks.swords.MakeSword;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 21:02
 */
public class Anvil {
	private static final int[] ID_ANVIL = {4046};
	private static final int[] ID_ANVIL_SWORD = {4047, 24664, 24677, 24678, 15520, 20258};
	private static final int[] ID_ANVIL_TRACK = {24820};
	final Area area = StayInArea.area;
	private MyMethodContext ctx;
	private int[] ids;
	private GameObject target = null;

	public Anvil(MyMethodContext ctx) {
		this.ctx = ctx;
		ids = getAnvilId();
	}

	public void click() {
		if (target == null) {
			for (GameObject anvil : ctx.objects.select(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					return area.contains(gameObject) && Arrays.binarySearch(ids, gameObject.getId()) >= 0;
				}
			}).shuffle().first()) {
				target = anvil;
			}
		}

		if (target != null) {
			if (!ctx.skillingInterface.isOpen() && ctx.camera.turnTo(target)) {
				if (ctx.skillingInterface.isProductionInterfaceOpen()) {
					ctx.skillingInterface.cancelProduction();
				}
				if (target.interact("Smith", "Anvil")) {
					Delay.sleep(400, 800);
					if (ctx.players.local().getAnimation() != -1 && !ctx.skillingInterface.isOpen()) {
						if (target.hover() && !ctx.skillingInterface.isOpen()) {
							ctx.menu.click("Smith", "Anvil");
						}
					}

					final Timer t = new Timer(Random.nextInt(5000, 6500));
					while (t.isRunning()) {
						if (ctx.skillingInterface.isOpen() || ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, MakeSword.WIDGET_SWORD_COOLDOWN).isValid()) {
							target = null;
							Delay.sleep(200, 800);
							break;
						}
						if (ctx.skillingInterface.isProductionInterfaceOpen()) {
							ctx.skillingInterface.cancelProduction();
						}
						Delay.sleep(100, 250);
					}
				}
			} else {
				Tile tile = ctx.movement.reachableNear(target);
				if (tile != Tile.NIL && ctx.movement.stepTowards(tile)) {
					Delay.sleep(500, 1500);
				}
			}
		}
	}

	private int[] getAnvilId() {
		switch (LogArtisanArmourer.instance.options.mode) {
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
