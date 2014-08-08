package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Hud;
import org.powerbot.script.rt6.Item;

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

	public DepositOre(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Deposit Ore";
	}

	@Override
	public boolean valid() {
		return super.valid()
				&& !ctx.skillingInterface.opened()
				&& (remainingIron() < nextDeposit[0] && !ctx.backpack.select().id(ID_IRON_NOTED).isEmpty())
				|| (remainingMithril() < nextDeposit[0] && !ctx.backpack.select().id(ID_MITHRIL_NOTED).isEmpty())
				|| (remainingAdamant() < nextDeposit[0] && !ctx.backpack.select().id(ID_ADAMANT_NOTED).isEmpty())
				|| (remainingRune() < nextDeposit[0] && !ctx.backpack.select().id(ID_RUNE_NOTED).isEmpty())
				|| (remainingCoal() < nextDeposit[1] && !ctx.backpack.select().id(ID_COAL_NOTED).isEmpty());
	}

	public int remainingAdamant() {
		return ctx.varpbits.varpbit(SETTING_ADAMANT_RUNE, 0xFFF);
	}

	public int remainingCoal() {
		return ctx.varpbits.varpbit(SETTING_COAL, 0x1FFF);
	}

	public int remainingIron() {
		return ctx.varpbits.varpbit(SETTING_IRON, 20, 0xFFF);
	}

	public int remainingMithril() {
		return ctx.varpbits.varpbit(SETTING_MITHRIL, 17, 0xFFF);
	}

	public int remainingRune() {
		return ctx.varpbits.varpbit(SETTING_ADAMANT_RUNE, 12, 0xFFF);
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
		final Item item = ctx.backpack.select().id(oreId).poll();

		options.isSmithing = false;
		//ArtisanArmourer.setStatus("Searching for smelter");

		final GameObject smelter = ctx.objects.select().id(options.mode == Mode.BURIAL_ARMOUR ? LogArtisanWorkshop.ID_SMELTER : LogArtisanWorkshop.ID_SMELTER_SWORDS).poll();

		if (!ctx.camera.prepare(smelter)) {
			return;
		}

		final int count = item.stackSize();
		options.status = "Depositing ore in smelter";

		if (ctx.hud.open(Hud.Window.BACKPACK) && item.interact("Use", item.name())) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.backpack.selectedItem().id() == oreId;
				}
			})) {
				Condition.sleep(250);
				if (smelter.interact("Use", item.name() + " -> Smelter")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return item.stackSize() < count;
						}
					});
					Condition.sleep(250);
				}
			}
		}
	}
}
