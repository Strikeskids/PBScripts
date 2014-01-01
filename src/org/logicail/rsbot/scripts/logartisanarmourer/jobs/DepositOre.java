package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

public class DepositOre extends ArtisanArmourerTask {
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
	private static final int[] nextDeposit = new int[]{Random.nextInt(500, 2000), Random.nextInt(500, 4000)};

	public DepositOre(LogArtisanArmourer script) {
		super(script);
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
		return super.activate()
				&& !ctx.skillingInterface.isOpen()
				&& (remainingIron() < nextDeposit[0] && !ctx.backpack.select().id(ID_IRON_NOTED).isEmpty())
				|| (remainingMithril() < nextDeposit[0] && !ctx.backpack.select().id(ID_MITHRIL_NOTED).isEmpty())
				|| (remainingAdamant() < nextDeposit[0] && !ctx.backpack.select().id(ID_ADAMANT_NOTED).isEmpty())
				|| (remainingRune() < nextDeposit[0] && !ctx.backpack.select().id(ID_RUNE_NOTED).isEmpty())
				|| (remainingCoal() < nextDeposit[1] && !ctx.backpack.select().id(ID_COAL_NOTED).isEmpty());
	}

	@Override
	public void run() {
		if (remainingIron() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(500, 2000);
			depositOre(ID_IRON_NOTED);
		}

		if (remainingMithril() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(500, 2000);
			depositOre(ID_MITHRIL_NOTED);
		}

		if (remainingAdamant() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(500, 2000);
			depositOre(ID_ADAMANT_NOTED);
		}

		if (remainingRune() < nextDeposit[0]) {
			nextDeposit[0] = Random.nextInt(500, 2000);
			depositOre(ID_RUNE_NOTED);
		}

		if (remainingCoal() < nextDeposit[1]) {
			nextDeposit[1] = Random.nextInt(500, 4000);
			depositOre(ID_COAL_NOTED);
		}
	}

	private void depositOre(final int oreId) {
		for (Item item : ctx.backpack.select().id(oreId).first()) {
			options.isSmithing = false;
			//ArtisanArmourer.setStatus("Searching for smelter");

			for (GameObject smelter : ctx.objects.select().id(options.mode == Mode.BURIAL_ARMOUR ? LogArtisanArmourer.ID_SMELTER : LogArtisanArmourer.ID_SMELTER_SWORDS).first()) {
				ctx.camera.turnTo(smelter);
				if (!smelter.isOnScreen()) {
					break;
				}

				final int count = item.getStackSize();
				options.status = "Depositing ore in smelter";

				if (item.interact("Use", item.getName())) {
					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.backpack.getSelectedItem().getId() == oreId;
						}
					})) {
						sleep(100, 500);
						if (smelter.interact("Use", item.getName() + " -> Smelter")) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									Item poll = ctx.backpack.select().id(oreId).poll();
									return poll.getStackSize() < count;
								}
							});
							sleep(100, 1000);
						}
					}
				}
			}
		}
	}
}
