package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.RenewFamiliar;
import org.powerbot.script.methods.Summoning;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Tile;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 19:52
 */
public class BankBob extends BankingAbstract {
	public BankBob(Banking bankingBranch) {
		super(bankingBranch);
	}

	@Override
	public String toString() {
		return "Banking: Beast of Burden";
	}

	@Override
	public boolean isValid() {
		return ctx.summoning.isOpen() || depositBob();
	}

	private boolean depositBob() {
		if (options.useBOB.get()) {
			if (!options.bobonce.get() || options.timesBob.get() == 0) {
				if (options.beastOfBurden.getBoBSpace() > 0 && ctx.summoning.isFamiliarSummoned() && ctx.summoning.getTimeLeft() >= 180 && ctx.backpack.isFull() && !getBackpackOffering().isEmpty()) {
					if (bankingBranch.beastOfBurdenCount.get() < options.beastOfBurden.getBoBSpace()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		if (ctx.summoning.isOpen()) {
			// Deposit BOB
			if (depositBob()) {
				options.status = "Deposit in familiar";
				if (ctx.summoning.isOpen()) {
					int countBefore = bankingBranch.beastOfBurdenCount.get();
					if (ctx.summoning.deposit(options.offering.getId(), 0)) {
						int newValue = ctx.summoning.getFamiliarStore().select().count();
						bankingBranch.beastOfBurdenCount.set(newValue);
						if (newValue != countBefore) {
							bankingBranch.fail.set(0);
						}
					}
				}
			} else {
				// Close BOB
				options.status = "Close familiar";
				ctx.summoning.close();
			}
		} else {
			// OPEN BOB
			bankingBranch.fail.set(0);
			options.status = "Open familiar";

			if (ctx.summoning.isFamiliarSummoned() && ctx.summoning.interactOrb(Summoning.Option.INTERACT)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return script.familiarFailed.get() || !ctx.chat.select().text("Store").isEmpty();
					}
				}, Random.nextInt(300, 600), 10);
				sleep(100, 800);
				final ChatOption option = ctx.chat.select().text("Store").poll();
				if (option.isValid()) {
					if (option.select(Random.nextBoolean())) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.summoning.isOpen();
							}
						}, Random.nextInt(550, 650), Random.nextInt(4, 12));
					}
				} else {
					final Tile start = ctx.players.local().getLocation();
					final List<Tile> tiles = RenewFamiliar.familarTile(ctx);
					for (final Tile tile : tiles) {
						script.familiarFailed.set(false);
						if (ctx.movement.findPath(tile).traverse()) {
							if (Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return IMovement.Euclidean(tile, ctx.players.local()) < 2.5;
								}
							}, Random.nextInt(400, 650), Random.nextInt(10, 15)) || !start.equals(start)) {
								if (ctx.summoning.open() || !script.familiarFailed.get()) {
									break;
								}
								sleep(600, 1800);
							}
						}
					}
				}
			}
		}
	}
}
