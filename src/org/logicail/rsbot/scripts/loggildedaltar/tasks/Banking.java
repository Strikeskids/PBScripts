package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
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
import org.powerbot.script.wrappers.Tile;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 21/08/12
 * Time: 19:57
 */
public class Banking extends LogGildedAltarTask {
	public static final int ID_MARRENTIL = 251;
	private static final int[] IDS_RING_OF_DUELLING = new int[]{2566, 2564, 2562, 2560, 2558, 2556, 2554, 2552};
	private static int beastOfBurdenWithdraws = 0; // TODO: Use later in altar class
	private static boolean withdrawnDelegation = false;
	private static int beastOfBurdenCount;
	private static int fail = 0;
	private final Timer timer = new Timer(1000);
	private final int[] ALWAYS_DEPOSIT = {
			114, 116, 118, 120, 122, 124, 126, 134, 136, 138, 314, 441, 448, 450, 454, 554, 555, 560, 561, 562, 877,
			882, 884, 886, 1704, 1973, 2327, 2429, 2433, 6961, 6962, 6963, 6965, 14665};
	private int pouchFailure;
	private Timer pouchTimer;
	private String cachedString = "Banking";

	public Banking(LogGildedAltar script) {
		super(script);
	}

	int getBeastOfBurdenCount() {
		return beastOfBurdenCount;
	}

	@Override
	public String toString() {
		return cachedString;
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
		return script.bankingTask.inBank();
	}

	@Override
	public void run() {
		if (ctx.widgets.get(13).isValid() || !ctx.game.isLoggedIn() || !ctx.isShutdown() || ctx.isPaused()) {
			return;
		}

		if (!timer.isRunning()) {
			cacheString();
			timer.reset();
		}

		if (script.summoningTask.isValid()) {
			return;
		}


		fail++;
		if (fail > 70) {
			StringBuilder reason = new StringBuilder();
			reason.append("Banking error\n\n");
			reason.append("State: ").append(getState()).append(" [").append(getState().ordinal()).append("]\n\n");

			if (getState() == State.WITHDRAWING) {
				if (options.lightBurners) {
					final String marrentilError = getMarrentilError();
					if (marrentilError != null) {
						reason.append(marrentilError).append("\n");
						ctx.log.info(marrentilError);
					}
				}

				final String bonesError = getBonesError();
				if (bonesError != null) {
					reason.append(bonesError);
					ctx.log.info(bonesError);
				}
			}

			ctx.stop(reason.toString());
			return;
		}

		switch (getState()) {
			case WALKING:
				fail = 0;
				break;
			case CLOSE_BANK:
				fail = 0;
				options.status = "Close bank";
				ctx.bank.close();
			case OPEN_BANK:
				fail = 0;
				options.status = "Open bank";
				if (!ctx.summoning.isOpen()) {
					for (Item item : ctx.equipment.select().id(ALWAYS_DEPOSIT)) {
						if (ctx.backpack.isFull()) {
							break;
						}
						ctx.equipment.unequip(item.getId());
					}
					if (!ctx.bank.open()) {
						ctx.camera.setAngle(Random.nextInt(0, 360));
					}
				}
				break;
			case WITHDRAWING:
				options.status = "Withdraw items";				/* If been trying to bank for too long stop */

				if (ctx.bank.select().isEmpty()) {
					options.status = "No items in bank";
					ctx.bank.close();
					break;
				}

				// Deposit items with no charge
				for (Item item : ctx.backpack.select().id(ALWAYS_DEPOSIT).shuffle()) {
					if (item.isValid()) {
						ctx.bank.deposit(item.getId(), Bank.Amount.ALL);
					}
				}

				// Wear ring of duelling
				if (!ctx.equipment.select().id(IDS_RING_OF_DUELLING).isEmpty()) {
					ctx.equipment.equip(IDS_RING_OF_DUELLING);
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
							break;
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

				if (!withdrawnDelegation) {
					withdrawnDelegation = true;
					withdrawRequiredItems(script.bankingTask);
					withdrawRequiredItems(script.houseTask);

					if (!ctx.bank.isOpen()) {
						break;
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

				if (options.bobonce && beastOfBurdenWithdraws == 0) {
					if (!ctx.backpack.isFull()) {
						if (!ctx.bank.withdraw(options.offering.getId(), Bank.Amount.ALL)) {
							ctx.log.info("Could not withdraw bones");
						} else {
							break;
						}
					}
				}

				if (options.lightBurners) {
					int marrentilCount = ctx.backpack.select().id(ID_MARRENTIL).count();
					if (marrentilCount < 2) {
						ctx.log.info("Withdraw clean marrentil");
						options.status = "Withdraw clean marrentil";
						if (ctx.backpack.isFull()) {
							ctx.bank.deposit(options.offering.getId(), 1);
							sleep(200, 600);
						}
						int tries = 5;
						while (ctx.backpack.select().id(ID_MARRENTIL).count() < 2 && !ctx.bank.select().id(ID_MARRENTIL).isEmpty()) {
							if (ctx.bank.withdraw(ID_MARRENTIL, 1)) {
								sleep(200, 400);
							}
							if (--tries <= 0) {
								break;
							}
						}

						if (ctx.backpack.select().id(ID_MARRENTIL).count() < 2) {
							options.status = "Could not withdraw clean marrentil";
							ctx.log.info(options.status);
						}
					}
					marrentilCount = ctx.backpack.select().id(ID_MARRENTIL).count();
					if (marrentilCount > 2) {
						options.status = "Depositing marrentil";
						ctx.bank.deposit(ID_MARRENTIL, 1);
						break;
					}
				}

				if (!ctx.backpack.isFull()) {
					if (!ctx.bank.withdraw(options.offering.getId(), 0)) {
						options.status = "Could not withdraw bones";
						ctx.log.info(options.status);
					} else {
						break;
					}
				}

				if (!getBackpackOffering().isEmpty() && (!options.lightBurners || (options.lightBurners && ctx.backpack.select().id(ID_MARRENTIL).count() >= 2))) {
					fail = 0;
					ctx.bank.close();
					options.status = "Finished banking";
					setBanking(false);
					return;
				}
				break;
			case OPEN_BOB:
				fail = 0;
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
						});
					}
				}
				break;
			case DEPOSIT_BOB:
				options.status = "Deposit in familiar";
				if (ctx.summoning.isOpen()) {
					int countBefore = beastOfBurdenCount;
					ctx.summoning.deposit(options.offering.getId(), 0);
					beastOfBurdenCount = ctx.summoning.getStore().select().count();
					if (beastOfBurdenCount != countBefore) {
						beastOfBurdenWithdraws++;
						fail = 0;
					}
				}
				break;
			case CLOSE_BOB:
				options.status = "Close familiar";
				if (ctx.summoning.isOpen()) {
					ctx.summoning.close();
				}
				break;
		}

		sleep(100, 250);
	}

	private void cacheString() {
		final StringBuilder state = new StringBuilder("Banking [").append(getState()).append("]");
		if (options.lightBurners) {
			state.append(" Marrentil(Inventory=").append(ctx.backpack.select().id(ID_MARRENTIL).count());
			if (ctx.bank.isOpen()) {
				state.append(" Bank=").append(ctx.bank.select().id(ID_MARRENTIL).count(true));
			}
			state.append(")");
		}

		state.append(" Bones(Inventory=").append(getBackpackOffering().count());
		if (ctx.bank.isOpen()) {
			state.append(" Bank=").append(ctx.bank.select().id(options.offering.getId()).count(true));
		}

		cachedString = state.append(")").toString();
	}

	private String getBonesError() {
		int bones = getBackpackOffering().count();
		int bonesBank = ctx.backpack.select().id(options.offering.getId()).count(true);

		if (bones + bonesBank < 28) {
			return "Could not find enough \"" + options.offering + "\" [Inventory=" + bones + " Bank=" + bonesBank + "]";
		}

		return null;
	}

	private String getMarrentilError() {
		int marrentil = ctx.backpack.select().id(ID_MARRENTIL).count();
		int marrentilBank = ctx.backpack.select().id(ID_MARRENTIL).count(true);

		if (marrentil + marrentilBank < 2) {
			return "Could not find enough \"Clean marrentil\" [Inventory=" + marrentil + " Bank=" + marrentilBank + "]";
		}

		return null;
	}

	State getState() {
		//if (Players.getLocal().isMoving()) {
		//	return State.WALKING;
		//}

		if (options.useBOB) {
			if (!options.bobonce || (options.bobonce && beastOfBurdenWithdraws == 0)) {
				if (options.beastOfBurden.getBoBSpace() > 0 && ctx.summoning.isFamiliarSummoned() && ctx.summoning.getTimeLeft() >= 180 && ctx.backpack.isFull() && !getBackpackOffering().isEmpty()) {
					if (getBeastOfBurdenCount() < options.beastOfBurden.getBoBSpace()) {
						if (ctx.summoning.isOpen()) {
							return State.DEPOSIT_BOB;
						}
						return State.OPEN_BOB;
					}
				}
			}
		}

		if (ctx.summoning.isOpen()) {
			return State.CLOSE_BOB;
		}

		if (!ctx.bank.isOpen()) {
			return State.OPEN_BANK;
		}

		return State.WITHDRAWING;
	}

	public void setBanking(boolean state) {
		options.banking = state;
		if (state) {
			beastOfBurdenWithdraws = 0;
			withdrawnDelegation = false;
			beastOfBurdenCount = 0;
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

	enum State {
		WALKING,
		CLOSE_BANK,
		OPEN_BANK,
		OPEN_BOB,
		DEPOSIT_BOB,
		CLOSE_BOB,
		WITHDRAWING
	}
}
