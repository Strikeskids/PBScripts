package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Tile;

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
		if (options.useBOB) {
			if (!options.bobonce || (bankingBranch.beastOfBurdenWithdraws == 0)) {
				if (options.beastOfBurden.getBoBSpace() > 0 && ctx.summoning.isFamiliarSummoned() && ctx.summoning.getTimeLeft() >= 180 && ctx.backpack.isFull() && !getBackpackOffering().isEmpty()) {
					if (bankingBranch.beastOfBurdenCount < options.beastOfBurden.getBoBSpace()) {
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
					int countBefore = bankingBranch.beastOfBurdenCount;
					if (ctx.summoning.deposit(options.offering.getId(), 0)) {
						bankingBranch.beastOfBurdenCount = ctx.summoning.getFamiliarStore().select().count();
					}
					if (bankingBranch.beastOfBurdenCount != countBefore) {
						bankingBranch.beastOfBurdenWithdraws++;
						bankingBranch.fail = 0;
					}
				}
			} else {
				// Close BOB
				options.status = "Close familiar";
				ctx.summoning.close();
			}
		} else {
			// OPEN BOB
			bankingBranch.fail = 0;
			options.status = "Open familiar";
			/*if (LocationAttribute.EDGEVILLE.isInLargeArea(ctx)) {
				final Npc npc = ctx.summoning.getNpc();
				if (npc.isValid() && Calculations.distanceTo(familiar) > 3 && Summoning.doCallFollower()) {
					sleep(250, 575);
				}
			}*/

			if (!ctx.summoning.open()) {
				final Tile start = ctx.players.local().getLocation();
				final Tile destination = start.randomize(-3, 0, -2, 2);
				final double distance = destination.distanceTo(start);
				if (distance > 1 && distance < 6 && ctx.movement.findPath(destination).traverse()) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return start.equals(ctx.players.local().getLocation());
						}
					}, Random.nextInt(550, 650), Random.nextInt(1, 4));
				}
			}
		}
	}
}
