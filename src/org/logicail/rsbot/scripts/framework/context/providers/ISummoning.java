package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.Summoning;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 17:00
 */
public class ISummoning extends Summoning {
	// TODO
	public static final int WIDGET_STORE = 671;
	public static final int WIDGET_STORE_CLOSE_BUTTON = 21;
	public static final int WIDGET_STORE_CLOSE_BUTTON_CHILD = 1;
	public static final int WIDGET_ORB = 1430;
	public static final int WIDGET_ORB_BUTTON = 5;

	protected IMethodContext ctx;

	private final IItemStore familiar;
	private final IItemStore backpack;

	public ISummoning(IMethodContext context) {
		super(context);
		ctx = context;
		familiar = new IItemStore(context, ctx.widgets.get(671, 25));
		backpack = new IItemStore(context, ctx.widgets.get(671, 30));
	}

	public IItemStore getFamiliarStore() {
		return familiar;
	}

	/*public IItemStore getBackpackStore() {
		return backpack;
	}*/

	public boolean canSummon(Familiar familiar) {
		return !ctx.backpack.select().id(familiar.getPouchId()).isEmpty() && getSummoningPoints() >= familiar.getRequiredPoints();
	}

	public boolean close() {
		if (!isOpen()) {
			return true;
		}

		final Component button = getCloseButton();
		return button.interact("Close") && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !isOpen();
			}
		});
	}

	public Component getCloseButton() {
		return ctx.widgets.get(WIDGET_STORE, WIDGET_STORE_CLOSE_BUTTON).getChild(WIDGET_STORE_CLOSE_BUTTON_CHILD);
	}

	/**
	 * Is Beast of burden interface open
	 *
	 * @return
	 */
	public boolean isOpen() {
		return getCloseButton().isValid();
	}

	public boolean deposit(final int id, int amount) {
		if (open()) {
			int spaceLeft = getFamiliar().getBoBSpace() - familiar.select().count();
			if (spaceLeft == 0) {
				return false;
			}
			final int backpackCount = backpack.select().id(id).count();
			for (Item item : backpack.shuffle().first()) {
				String action = "Store-" + amount;
				if (backpackCount == 0) {
					return false;
				}
				if (backpackCount < amount || amount == 0 || backpackCount < spaceLeft) {
					action = "Store-All";
				}
				// TODO: Reenable
				/*if (hasAction(item, action)) {
					if (item.interact(action)) {
						return true;
					}
				} else*/
				if (item.interact("Store-X") && ctx.chat.waitForInputWidget()) {
					sleep(800, 1200);
					if (amount == 0) {
						amount = Random.nextInt(backpackCount, backpackCount * Random.nextInt(2, 5));
					}
					if (ctx.chat.isInputWidgetOpen()) {
						ctx.log.info("KB send " + amount + " == " + ctx.keyboard.sendln(amount + ""));
					}
				}
			}
		}

		return false;
	}

	private void closeBankIfInTheWay() {
		final Component bank = ctx.bank.getWidget();
		if (bank.isValid()) {
			final Component orb = getOrb();
			if (orb.isValid() && bank.getBoundingRect().intersects(orb.getBoundingRect())) {
				sleep(50, 250);
				ctx.log.info("Bank interface in the way of orb closing bank");
				ctx.bank.close();
				sleep(200, 1000);
			}
		}
	}

	@Override
	public boolean renewFamiliar() {
		closeBankIfInTheWay();
		return super.renewFamiliar();
	}

	public boolean summon(Familiar familiar) {
		if (!canSummon(familiar)) {
			return false;
		}

		// Close bank
		if (ctx.bank.isOpen()) {
			ctx.bank.close();
			sleep(400, 1200);
		}

		final Item pouch = ctx.backpack.shuffle().poll();
		if (ctx.hud.view(Hud.Window.BACKPACK)) {
			sleep(200, 800);
			return pouch.isValid() && pouch.interact("Summon") && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.summoning.isFamiliarSummoned() && !pouch.isValid();
				}
			}, Random.nextInt(500, 700), Random.nextInt(4, 8));
		}

		return false;
	}

	public boolean hasAction(final Item item, final String action) {
		if (item.hover()) {
			for (String a : ctx.menu.getItems()) {
				if (a != null && a.matches("^" + action + "(<.*>)?")) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean open() {
		if (isOpen()) {
			return true;
		}

		if (isFamiliarSummoned() && interactOrb("Interact")) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !ctx.chat.select().text("Store").isEmpty();
				}
			})) {
				for (ChatOption option : ctx.chat.select().text("Store")) {
					sleep(100, 800);
					if (option.select(Random.nextBoolean())) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return isOpen();
							}
						}, Random.nextInt(550, 650), Random.nextInt(4, 12));
					}
				}
			}
		}

		return isOpen();
	}

	public boolean interactOrb(String action) {
		// Bank in the way of orb
		closeBankIfInTheWay();

		final Component orb = getOrb();
		return orb.isValid() && orb.isVisible() && orb.interact(action);
	}

	public Component getOrb() {
		return ctx.widgets.get(WIDGET_ORB, WIDGET_ORB_BUTTON);
	}
}
