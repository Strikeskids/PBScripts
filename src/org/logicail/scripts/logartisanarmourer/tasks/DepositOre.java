package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Item;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:03
 */
public class DepositOre extends Node {
	private static final int SETTING_IRON = 125;
	//private static final int[] IDS_ORES = {441, 448, 450, 452, 454};
	private static final int SETTING_MITHRIL = 126;
	private static final int SETTING_ADAMANT_RUNE = 127;
	private static final int SETTING_COAL = 128;
	private static final int ID_IRON_NOTED = 441;
	private static final int ID_MITHRIL_NOTED = 448;
	private static final int ID_ADAMANT_NOTED = 450;
	private static final int ID_RUNE_NOTED = 452;
	private static final int ID_COAL_NOTED = 454;
	private static final int[] nextDeposit = new int[]{Random.nextInt(400, 1800), Random.nextInt(750, 3500)};
	private final LogArtisanArmourerOptions options;

	public DepositOre(LogicailMethodContext ctx) {
		super(ctx);
		this.options = ((LogArtisanArmourer) ctx.script).options;
	}

	public int remainingIron() {
		return ctx.settings.get(SETTING_IRON, 20, 0xFFF);
	}

	public int remainingMithril() {
		return ctx.settings.get(SETTING_MITHRIL, 17, 0xFFF);
	}

	public int remainingAdamant() {
		return ctx.settings.get(SETTING_ADAMANT_RUNE, 0xFFF);
	}

	public int remainingRune() {
		return ctx.settings.get(SETTING_ADAMANT_RUNE, 12, 0xFFF);
	}

	public int remainingCoal() {
		return ctx.settings.get(SETTING_COAL, 0x1FFF);
	}

	@Override
	public String toString() {
		return "Deposit Ore";
	}

	@Override
	public boolean activate() {
		return !ctx.skillingInterface.isOpen()
				&& (remainingIron() < nextDeposit[0] && !ctx.backpack.select().id(ID_IRON_NOTED).isEmpty()
				|| (remainingMithril() < nextDeposit[0] && !ctx.backpack.select().id(ID_MITHRIL_NOTED).isEmpty())
				|| (remainingAdamant() < nextDeposit[0] && !ctx.backpack.select().id(ID_ADAMANT_NOTED).isEmpty())
				|| (remainingRune() < nextDeposit[0] && !ctx.backpack.select().id(ID_RUNE_NOTED).isEmpty())
				|| (remainingCoal() < nextDeposit[1] && !ctx.backpack.select().id(ID_COAL_NOTED).isEmpty())
		) && !ctx.objects.select().id(options.mode == Mode.BURIAL_ARMOUR ? LogArtisanArmourer.ID_SMELTER : LogArtisanArmourer.ID_SMELTER_SWORDS).first().isEmpty();
	}

	@Override
	public void execute() {
		if (remainingIron() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(400, 1800);
			depositOre(ID_IRON_NOTED);
		}

		if (remainingMithril() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(400, 1800);
			depositOre(ID_MITHRIL_NOTED);
		}

		if (remainingAdamant() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(400, 1800);
			depositOre(ID_ADAMANT_NOTED);
		}

		if (remainingRune() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(400, 1800);
			depositOre(ID_RUNE_NOTED);
		}

		if (remainingCoal() < nextDeposit[1]) {
			nextDeposit[1] = Random.nextInt(750, 3500);
			depositOre(ID_COAL_NOTED);
		}
	}

	private void depositOre(final int oreId) {
		for (Item item : ctx.backpack.select().id(oreId).first()) {
			options.isSmithing = false;

			for (GameObject smelter : ctx.objects) {
				if (ctx.camera.turnTo(smelter)) {
					final int count = item.getStackSize();
					if (item.interact("Use")) {
						sleep(100, 500);
						if (smelter.interact("Use", item.getName() + " -> Smelter")) {
							final Timer t = new Timer(Random.nextInt(4000, 8000));
							while (t.isRunning()) {
								if (item.getStackSize() != count) {
									break;
								}
								sleep(50, 300);
							}
							sleep(100, 1000);
						}
					}
				} else {
					ctx.movement.stepTowards(smelter.getLocation().randomize(2, 2));
					sleep(600, 1600);
				}
			}
		}
	}
}

