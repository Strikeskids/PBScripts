package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.logicail.rsbot.util.ItemHelper;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/01/14
 * Time: 12:59
 */
public class SummoningPotion extends NodePath {
	public static final int[] SUMMONING_POTION = {12146, 12144, 12142, 12140, 23621, 23623, 23625, 23627, 23629, 23631};

	public SummoningPotion(LogGildedAltar script) {
		super(script, Path.SUMMONING_ANYWHERE);
	}

	@Override
	protected boolean doLarge() {
		return false;
	}

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		List<BankRequiredItem> list = new ArrayList<BankRequiredItem>();

		if (ctx.backpack.select().id(SUMMONING_POTION).isEmpty()) {
			list.add(new BankRequiredItem(1, false, null, SUMMONING_POTION));
		}

		return list;
	}

	@Override
	public boolean isValid() {
		return getItemsNeededFromBank().isEmpty();
	}

	@Override
	public void run() {
		if (ctx.bank.isOpen() && ctx.bank.close()) {
			sleep(200, 800);
		}

		final Item potion = ItemHelper.getFirst(ctx.backpack, SUMMONING_POTION, 1);
		if (ctx.hud.view(Hud.Window.BACKPACK)) {
			if (potion.isValid() && potion.interact("Drink")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !script.summoningTask.branch();
					}
				}, Random.nextInt(200, 500), Random.nextInt(5, 10));
			}
		}

		sleep(400, 1300);
	}
}
