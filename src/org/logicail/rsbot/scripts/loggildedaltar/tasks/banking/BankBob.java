package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.RenewFamiliar;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ChatOption;
import org.powerbot.script.rt6.Npc;
import org.powerbot.script.rt6.Summoning;

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
	public boolean valid() {
		return ctx.summoning.isOpen() || depositBob();
	}

	private boolean depositBob() {
		if (options.useBOB.get()) {
			if (!options.bobonce.get() || options.timesBob.get() == 0) {
				if (options.beastOfBurden.bobSpace() > 0 && ctx.summoning.summoned() && ctx.summoning.timeLeft() >= 180 && ctx.backpack.isFull() && !getBackpackOffering().isEmpty()) {
					if (bankingBranch.beastOfBurdenCount.get() < options.beastOfBurden.bobSpace()) {
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
					int spaceLeft = options.beastOfBurden.bobSpace() - ctx.summoning.getFamiliarStore().select().count();
					if (spaceLeft <= 0 || ctx.summoning.deposit(options.offering.getId(), 0)) {
						int count = ctx.summoning.getFamiliarStore().select().count();
						script.log.info("Set BoB count: " + count);
						bankingBranch.beastOfBurdenCount.set(count);
					}
				}
			} else {
				// Close BOB
				options.status = "Close familiar";
				ctx.summoning.close();
			}
		} else {
			// OPEN BOB
			options.status = "Open familiar";
			script.familiarFailed.set(false);

			if (ctx.summoning.summoned() && ctx.summoning.select(Summoning.Option.INTERACT)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return script.familiarFailed.get() || !ctx.chat.select().text("Store").isEmpty();
					}
				}, 200, 20);
				ctx.sleep(300);
				final ChatOption option = ctx.chat.select().text("Store").poll();
				if (option.valid()) {
					if (option.select(Random.nextBoolean())) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.summoning.isOpen();
							}
						}, 200, 9);
					}
				} else {
					final Npc npc = ctx.summoning.npc();
					final double distance = IMovement.Euclidean(npc, ctx.players.local());
					if (ctx.summoning.summoned() && distance > 4) {
						if (ctx.summoning.select(Summoning.Option.CALL_FOLLOWER)) {
							if (Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return IMovement.Euclidean(npc, ctx.players.local()) < distance;
								}
							}, 250, 10)) {
								ctx.sleep(300);
								return;
							}
						}
					}

					final Tile start = ctx.players.local().tile();
					final List<Tile> tiles = RenewFamiliar.familarTile(ctx);
					for (final Tile tile : tiles) {
						script.familiarFailed.set(false);
						if (ctx.movement.findPath(tile).traverse()) {
							if (Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return IMovement.Euclidean(tile, ctx.players.local()) < 2.5;
								}
							}, 300, Random.nextInt(10, 15)) || !start.equals(start)) {
								if (ctx.summoning.open() || !script.familiarFailed.get()) {
									break;
								}
								ctx.sleep(900);
							}
						}
					}
				}
			}
		}
	}
}
