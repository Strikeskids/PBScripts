package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.RenewFamiliar;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
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
			if (!options.bobonce.get() || (bankingBranch.beastOfBurdenWithdraws.get() == 0)) {
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
						bankingBranch.beastOfBurdenCount.set(ctx.summoning.getFamiliarStore().select().count());
					}
					if (bankingBranch.beastOfBurdenCount.get() != countBefore) {
						bankingBranch.beastOfBurdenWithdraws.incrementAndGet();
						bankingBranch.fail.set(0);
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

			if (!ctx.summoning.open()) {
				// If edgeville

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
