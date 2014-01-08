package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.methods.Summoning;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 20:17
 */
public class BankWithdraw extends BankingAbstract {
	private int pouchFailure;
	private Timer pouchTimer;

	public BankWithdraw(Banking banking) {
		super(banking);
	}

	@Override
	public String toString() {
		return "Banking: Withdraw";
	}

	// TODO: Check this
	private void createSpaceInInventory(int min, int max) {
		if (isSpace(min)) {
			if (!ctx.bank.deposit(options.offering.getId(), Random.nextInt(min, max))) {
				sleep(200, 800);
				ctx.bank.deposit(options.offering.getId(), Random.nextInt(min, max));
			}
			sleep(200, 800);
		}
	}

	private boolean isSpace(int space) {
		return getSpace() < space;
	}

	private int getSpace() {
		return 28 - ctx.backpack.select().count();
	}

	@Override
	public boolean isValid() {
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
		for (Item item : ctx.backpack.select().id(Banking.ALWAYS_DEPOSIT).shuffle()) {
			if (item.isValid()) {
				ctx.bank.deposit(item.getId(), Bank.Amount.ALL);
			}
		}

		// Wear ring of duelling
		if (!ctx.equipment.select().id(Banking.IDS_RING_OF_DUELLING).isEmpty()) {
			ctx.equipment.equip(Banking.IDS_RING_OF_DUELLING);
		}

        /* Summoning Pouch */
		// TODO: made renew familar a loop job
		if (options.useBOB && options.beastOfBurden.getBoBSpace() > 0) {
			if (ctx.summoning.isFamiliarSummoned()) {
				Summoning.Familiar familiar = ctx.summoning.getFamiliar();

				if (familiar != null && familiar != options.beastOfBurden) {
					options.status = "Dismiss wrong familiar";
					ctx.summoning.dismissFamiliar();
					sleep(200, 800);
					return;
				}
			}

                    /* Disable summoning if don't have level required */
			if (options.beastOfBurden.getRequiredLevel() > ctx.skills.getRealLevel(Skills.SUMMONING)) {
				ctx.log.info("Summoning level too low -> Disabling summoning");
				options.useBOB = false;
			} else {
				if (ctx.summoning.getTimeLeft() <= 300 || !ctx.summoning.isFamiliarSummoned()) { // If 5 minutes left take a pouch
					if (ctx.backpack.select().id(options.beastOfBurden.getPouchId()).isEmpty() && !ctx.bank.select().id(options.beastOfBurden.getPouchId()).isEmpty()) {
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
						if (!ctx.bank.withdraw(options.beastOfBurden.getPouchId(), 1)) {
							ctx.log.info("Can't withdraw pouch");
						}
					}

					if (ctx.summoning.canSummon(options.beastOfBurden)) {
						if (ctx.summoning.isFamiliarSummoned()) {
							if (ctx.summoning.renewFamiliar()) {
								sleep(600, 1600);
							}
						} else {
							if (ctx.bank.close()) {
								sleep(1200, 2400);

								if (pouchTimer != null) {
									if (!pouchTimer.isRunning()) {
										pouchTimer = null;
										pouchFailure = 0;
									}
								}

								if (pouchTimer == null) {
									for (Item pouch : ctx.backpack.select().id(options.beastOfBurden.getPouchId()).first()) {
										if (!ctx.hud.isVisible(Hud.Window.BACKPACK) && ctx.hud.view(Hud.Window.BACKPACK)) {
											sleep(200, 800);
										}
										if (pouch.isValid() && pouch.interact("Summon")) {
											if (!Condition.wait(new Callable<Boolean>() {
												@Override
												public Boolean call() throws Exception {
													return ctx.summoning.isFamiliarSummoned();
												}
											}) && !ctx.backpack.select().id(options.beastOfBurden.getPouchId()).isEmpty()) {
												pouchFailure++;
												if (pouchFailure > 3) {
													pouchTimer = new Timer(30000);
												}
											} else {
												pouchFailure = 0;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (!bankingBranch.withdrawnDelegation) {
			bankingBranch.withdrawnDelegation = true;
			withdrawRequiredItems(script.bankingTask);
			withdrawRequiredItems(script.houseTask);

			if (!ctx.bank.isOpen()) {
				return;
			}
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
							if (MyEquipment.equip(Settings.aura.getId())) {
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

		if (options.bobonce && bankingBranch.beastOfBurdenWithdraws == 0) {
			if (!ctx.backpack.isFull()) {
				if (!ctx.bank.withdraw(options.offering.getId(), Bank.Amount.ALL)) {
					ctx.log.info("Could not withdraw bones");
				} else {
					return;
				}
			}
		}

		if (options.lightBurners) {
			int marrentilCount = ctx.backpack.select().id(Banking.ID_MARRENTIL).count();
			if (marrentilCount < 2) {
				ctx.log.info("Withdraw clean marrentil");
				options.status = "Withdraw clean marrentil";
				if (ctx.backpack.isFull()) {
					ctx.bank.deposit(options.offering.getId(), 1);
					sleep(200, 600);
				}
				int tries = 5;
				while (ctx.backpack.select().id(Banking.ID_MARRENTIL).count() < 2 && !ctx.bank.select().id(Banking.ID_MARRENTIL).isEmpty()) {
					if (ctx.bank.withdraw(Banking.ID_MARRENTIL, 1)) {
						sleep(200, 400);
					}
					if (--tries <= 0) {
						break;
					}
				}

				if (ctx.backpack.select().id(Banking.ID_MARRENTIL).count() < 2) {
					options.status = "Could not withdraw clean marrentil";
					ctx.log.info(options.status);
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
				ctx.log.info(options.status);
			} else {
				return;
			}
		}

		if (!getBackpackOffering().isEmpty() && (!options.lightBurners || (options.lightBurners && ctx.backpack.select().id(Banking.ID_MARRENTIL).count() >= 2))) {
			bankingBranch.fail = 0;
			ctx.bank.close();
			options.status = "Finished banking";
			bankingBranch.setBanking(false);
		}
	}

	private void withdrawRequiredItems(Branch delegation) {
		for (Object node : delegation.getNodes()) {
			if (script.getController().isStopping() || script.getController().isSuspended()) {
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
					for (int id : bankRequiredItem.getIds()) {
						if (bankRequiredItem.getQuantity() == 0) {
							createSpaceInInventory(1);
						} else {
							createSpaceInInventory(bankRequiredItem.getQuantity());
						}

						for (Item item : ctx.bank.select().id(id).first()) {
							if (ctx.bank.withdraw(id, Math.min(item.getStackSize(), bankRequiredItem.getQuantity()))) {// Possible bug, if quantity > 1
								success = true;
								sleep(200, 600);
								if (bankRequiredItem.equip()) {
									if (ctx.equipment.equip(id)) {
										sleep(100, 300);
									}
								}
								break;
							}
						}
					}
					if (!success) {
						ctx.log.info("Ran out of required item for: " + ((NodePath) node).getPath().getLocation().name());
					} else {
						sleep(100, 300);
					}
				}
			}
		}
	}

	private void createSpaceInInventory(int min) {
		createSpaceInInventory(min, min);
	}
}