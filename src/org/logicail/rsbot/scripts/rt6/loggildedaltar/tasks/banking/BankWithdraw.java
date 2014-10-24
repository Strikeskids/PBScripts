package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.banking;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.RenewFamiliar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.SummoningPotion;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Bank;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.Skills;
import org.powerbot.script.rt6.Summoning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 20:17
 */
public class BankWithdraw extends BankingAbstract {
	private static final int[] RENEW_TIMES = {360, 330, 300, 270};
	private int nextRenew;

	public BankWithdraw(Banking banking) {
		super(banking);
		reset();
	}

	public final void reset() {
		nextRenew = RENEW_TIMES[Random.nextInt(0, RENEW_TIMES.length)];
	}

	@Override
	public String toString() {
		return "Banking: Withdraw";
	}

	// TODO: Check this
	private void createSpaceInInventory(int min, int max) {
		if (isNotSpace(min)) {
			if (!ctx.bank.deposit(options.offering.getId(), Random.nextInt(min, max))) {
				Condition.sleep(333);
				ctx.bank.deposit(options.offering.getId(), Random.nextInt(min, max));
			}
			Condition.sleep(100);
		}
	}

	private boolean isNotSpace(int slotsRequired) {
		return getSpace() < slotsRequired;
	}

	private int getSpace() {
		return 28 - ctx.backpack.select().count();
	}

	@Override
	public boolean valid() {
		return true;
	}

	@Override
	public void run() {
		options.status = "Withdraw items";				/* If been trying to bank for too long stop */

		if (ctx.bank.select().isEmpty()) {
			options.status = "No items in bank";
			ctx.bank.close();
			return;
		}

		// Deposit items with no charge
		for (Item item : ctx.backpack.select().id(Banking.ALWAYS_DEPOSIT, SummoningPotion.SUMMONING_POTION).shuffle()) {
			if (item.valid()) {
				ctx.bank.deposit(item.id(), Bank.Amount.ALL);
			}
		}

		// Wear ring of duelling
		if (!ctx.equipment.select().id(Banking.IDS_RING_OF_DUELLING).isEmpty()) {
			ctx.equipment.equip(Banking.IDS_RING_OF_DUELLING);
		}

        /* Summoning Pouch */
		// TODO: made renew familar a loop job
		if (options.useBOB.get() && options.beastOfBurden.bobSpace() > 0) {
			if (ctx.summoning.summoned()) {
				Summoning.Familiar familiar = ctx.summoning.familiar();

				if (familiar != null && familiar != options.beastOfBurden) {
					options.status = "Dismiss wrong familiar";
					ctx.summoning.dismiss();
					return;
				}
			}

            /* Disable summoning if don't have level required */
			if (options.beastOfBurden.requiredLevel() > ctx.skills.realLevel(Skills.SUMMONING)) {
				script.log.info("Summoning level too low -> Disabling summoning");
				options.useBOB.set(false);
			} else {
				if (ctx.summoning.timeLeft() <= nextRenew || !ctx.summoning.summoned()) { // If 5 minutes left take a pouch
					if (ctx.backpack.select().id(options.beastOfBurden.pouchId()).isEmpty() && !ctx.bank.select().id(options.beastOfBurden.pouchId()).isEmpty()) {
						if (ctx.backpack.isFull()) {
							if (ctx.bank.deposit(options.offering.getId(), 1)) {
								Condition.wait(new Callable<Boolean>() {
									@Override
									public Boolean call() throws Exception {
										return ctx.backpack.isFull();
									}
								});
							}
						}
						if (ctx.bank.withdraw(options.beastOfBurden.pouchId(), 1)) {
							reset();
						} else {
							script.log.info("Can't withdraw pouch");
						}
					}

					if (script.nextSummon.get() < System.currentTimeMillis() && ctx.summoning.canSummon(options.beastOfBurden)) {
						RenewFamiliar.renew(script);
						return;
					}
				}
			}
		}

		if (!bankingBranch.withdrawnDelegation.get()) {
			List<Branch> list = new ArrayList<Branch>(
					Arrays.asList(new Branch[]{script.bankingTask, script.houseTask})
			);
			if (script.summoningTask.branch()) {
				list.add(script.summoningTask);
			}
			Collections.shuffle(list);
			for (Branch branch : list) {
				withdrawRequiredItems(branch);
				if (!ctx.bank.opened()) {
					return;
				}
			}

			bankingBranch.withdrawnDelegation.set(true);
		}

		// Aura disabled for now
		/*if (options.useAura && Settings.aura != null) {
			MyAuras.Aura aura = MyAuras.getAura();
			if (aura != Settings.aura) {
				instance.getLogHandler().print("Not wearing correct aura");

				if (!Inventory.contains(Settings.aura.getId())) {
					Bank.withdraw(Settings.aura.getId(), 1);
					Task.sleep(50, 500);
				}
				if (Inventory.contains(Settings.aura.getId())) {
					if (IEquipment.equip(Settings.aura.getId())) {
						Task.sleep(50, 500);
					}
				}
			}
			final Item item = Inventory.getItem(MyAuras.getIds(Settings.aura.getId()));
			if (item != null) {
				Bank.deposit(item.getId(), 1);
				Task.sleep(50, 500);
			}
		}*/

		if (options.lightBurners.get()) {
			int marrentilCount = ctx.backpack.select().id(Banking.ID_MARRENTIL).count();
			if (marrentilCount < 2) {
				script.log.info("Withdraw clean marrentil");
				options.status = "Withdraw clean marrentil";
				if (ctx.backpack.isFull()) {
					ctx.bank.deposit(options.offering.getId(), 1);
					Condition.sleep(200);
				}
				int tries = 5;
				while (ctx.backpack.select().id(Banking.ID_MARRENTIL).count() < 2 && !ctx.bank.select().id(Banking.ID_MARRENTIL).isEmpty()) {
					if (ctx.bank.withdraw(Banking.ID_MARRENTIL, 1)) {
						Condition.sleep(100);
					}
					if (--tries <= 0) {
						break;
					}
				}

				if (ctx.backpack.select().id(Banking.ID_MARRENTIL).count() < 2) {
					options.status = "Could not withdraw clean marrentil";
					script.log.info(options.status);
				}
			}
			marrentilCount = ctx.backpack.select().id(Banking.ID_MARRENTIL).count();
			if (marrentilCount > 2) {
				options.status = "Depositing marrentil";
				ctx.bank.deposit(Banking.ID_MARRENTIL, 1);
				return;
			}
		}

		if (!ctx.backpack.isFull()) {
			if (!ctx.bank.withdraw(options.offering.getId(), 0)) {
				options.status = "Could not withdraw bones";
				script.log.info(options.status);
			} else {
				return;
			}
		}

		// Fill BOB
		if (options.useBOB.get() && ctx.summoning.summoned() && ctx.summoning.timeLeft() >= 180 && !getBackpackOffering().isEmpty()) {
			if (bankingBranch.beastOfBurdenCount.get() != options.beastOfBurden.bobSpace()) {
				// Withdraw-X to BoB
				if (ctx.bank.withdrawBoB(options.offering.getId(), Random.nextInt(options.beastOfBurden.bobSpace(), options.beastOfBurden.bobSpace() * 4))) {
					bankingBranch.beastOfBurdenCount.set(options.beastOfBurden.bobSpace());
					return;
				}
			}
		}

		if (!getBackpackOffering().isEmpty()
				&& (!options.lightBurners.get() || (options.lightBurners.get() && ctx.backpack.select().id(Banking.ID_MARRENTIL).count() >= 2))) {
			ctx.bank.close();
			options.status = "Finished banking";
			bankingBranch.setBanking(false);
		}
	}

	private void withdrawRequiredItems(Branch delegation) {
		for (Object node : delegation.getNodes()) {
			if (ctx.controller.isStopping() || ctx.controller.isSuspended()) {
				return;
			}
			if (!(node instanceof NodePath)) {
				continue;
			}
			NodePath nodePath = (NodePath) node;

			boolean success = false;
			for (BankRequiredItem bankRequiredItem : nodePath.getItemsNeededFromBank()) {
				if (bankRequiredItem != null) {
					if (bankRequiredItem.equip() && !ctx.equipment.select().id(bankRequiredItem.getIds()).isEmpty()) {
						continue;
					}
					for (final int id : bankRequiredItem.getIds()) {
						createSpaceInInventory(Math.max(1, bankRequiredItem.getQuantity()));

						for (Item item : ctx.bank.select().id(id).first()) {
							if (success) {
								break;
							}
							int quantity = Math.min(item.stackSize(), bankRequiredItem.getQuantity());
							if (ctx.bank.withdraw(id, quantity)) {// Possible bug, if quantity > 1
								success = true;
								Condition.sleep(200);
								if (bankRequiredItem.equip()) {
									if (ctx.equipment.equip(id)) {
										if (!ctx.bank.opened()) {
											Condition.sleep(200);
											ctx.bank.open();
											Condition.sleep(400);
										}
										if (!ctx.bank.opened()) {
											return;
										}
									}
								}
								break;
							} else {
								if (!ctx.bank.select().id(id).isEmpty()) {
									Condition.sleep(200);
									script.log.info("ctx.bank.withdraw(" + id + ", " + quantity + ") reports fail");
								}
							}
						}
					}
					if (!success) {
						script.log.info("Ran out of required item for: " + nodePath.getPath().getLocation().name());
					} else {
						Condition.sleep(150);
					}
				}
			}
		}
	}

	private void createSpaceInInventory(int min) {
		createSpaceInInventory(min, min);
	}
}
