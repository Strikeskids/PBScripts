package org.logicail.rsbot.scripts.framework.context.rt6.providers;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 17:00
 */
public class ISummoning extends Summoning {
	public static final int WIDGET_STORE = 671;
	public static final int WIDGET_STORE_FAMILIAR = 25;
	public static final int WIDGET_STORE_BACKPACK = 30;
	public static final int WIDGET_STORE_CLOSE_BUTTON = 21;
	public static final int WIDGET_STORE_CLOSE_BUTTON_CHILD = 1;
	public static final int WIDGET_ORB = 1430;
	public static final int WIDGET_ORB_BUTTON = 23;
	protected final IClientContext ctx;
	private final IItemStore familiar;
	private final IItemStore backpack;

	public ISummoning(IClientContext context) {
		super(context);
		ctx = context;
		familiar = new IItemStore(context, ctx.widgets.component(WIDGET_STORE, WIDGET_STORE_FAMILIAR));
		backpack = new IItemStore(context, ctx.widgets.component(WIDGET_STORE, WIDGET_STORE_BACKPACK));
	}

	private static String getStoreString(int amount) {
		switch (amount) {
			case 0:
				return "Store-All";
			case 1:
			case 5:
			case 10:
				return "Store-" + amount;
			default:
				return "Store-X";
		}
	}

	public boolean close() {
		if (!isOpen()) {
			return true;
		}

		return getCloseButton().interact("Close") && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !isOpen();
			}
		}, Random.nextInt(200, 400), Random.nextInt(5, 10));
	}

	public Component getCloseButton() {
		return ctx.widgets.component(WIDGET_STORE, WIDGET_STORE_CLOSE_BUTTON).component(WIDGET_STORE_CLOSE_BUTTON_CHILD);
	}

	/**
	 * Is Beast of burden interface open
	 *
	 * @return
	 */
	public boolean isOpen() {
		return familiar.isOpen();
	}

	public boolean deposit(final int id, int amount) {
		if (open()) {
			int spaceLeft = familiar().bobSpace() - familiar.select().count();
			if (spaceLeft == 0) {
				ctx.controller.script().log.info("No space left in familiar");
				return false;
			}

			final int familiarStartCount = familiar.select().count();
			final int backpackCount = backpack.select().id(id).count();
			final Item item = backpack.shuffle().poll();
			final String action = getStoreString(amount);
			if (action.equals("Store-X")) {
				ctx.controller.script().log.info("deposit: Store-X");
				if (item.interact(new Filter<Menu.Command>() {
					@Override
					public boolean accept(Menu.Command entry) {
						return entry.action.startsWith(action) && entry.option.startsWith(item.name());
					}
				}) && ctx.chat.waitForInputWidget()) {
					ctx.sleep(500);
					if (amount == 0) {
						amount = Random.nextInt(backpackCount, (int) (backpackCount * Random.nextDouble(1.0, 5.0)));
					}
					if (ctx.chat.isInputWidgetOpen() && ctx.input.sendln(amount + "")) {
						return Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return familiar.select().count() != familiarStartCount;
							}
						}, 100, 15);
					}
				}
			}
			return item.interact(new Filter<Menu.Command>() {
				@Override
				public boolean accept(Menu.Command entry) {
					final String name = item.name();
					if (entry.action.startsWith(action) && (name == null || entry.option.startsWith(name))) {
						ctx.controller.script().log.info("deposit: \"" + entry.action + ", " + entry.option + "\"");
						return true;
					}
					return false;
				}
			}) && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return familiar.select().count() != familiarStartCount;
				}
			}, 100, 15);
		}

		return false;
	}

	public boolean open() {
		if (isOpen()) {
			return true;
		}

		if (summoned() && select(Option.INTERACT)) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !ctx.chat.select().text("Store").isEmpty();
				}
			})) {
				for (ChatOption option : ctx.chat.first()) {
					ctx.sleep(200);
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

	public boolean select(Option option) {
		if (option == null) {
			return false;
		}

		closeBankIfInTheWay();

		return super.select(option);
	}

	private void closeBankIfInTheWay() {
		if (ctx.bank.opened()) {
			final Component bank = ctx.bank.getWidget();
			if (bank.valid()) {
				final Component orb = ctx.widgets.component(WIDGET_ORB, WIDGET_ORB_BUTTON);
				if (bank.boundingRect().intersects(orb.boundingRect())) {
					ctx.bank.close();
					ctx.sleep(75);
				}
			}
		}
	}

	@Override
	public boolean dismiss() {
		return summoned() && select(Option.DISMISS) && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !summoned();
			}
		}, 50, 20);
	}

	public IItemStore getFamiliarStore() {
		return familiar;
	}

//	public boolean hasAction(final Item item, final String action) {
//		if (item.hover()) {
//			for (String a : ctx.menu.getItems()) {
//				if (a != null && a.matches("^" + action + "(<.*>)?")) {
//					//ctx.log.info("Action: \"" + a + "\"");
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	@Override
	public boolean renew() {
		return select(Option.RENEW_FAMILIAR);
	}

	public boolean canSummon(Familiar familiar) {
		return familiar != null
				&& points() >= familiar.requiredPoints()
				&& (!summoned() || timeLeft() <= 150)
				&& !ctx.backpack.select().id(familiar.pouchId()).isEmpty();
	}

	/**
	 * Note returns straight away
	 *
	 * @param familiar
	 * @return
	 */
	public boolean summon(Familiar familiar) {
		if (!canSummon(familiar)) {
			return false;
		}

		// Close bank
		if (ctx.bank.opened()) {
			ctx.bank.close();
			ctx.sleep(500);
		}

		final Item pouch = ctx.backpack.shuffle().poll();
		if (ctx.hud.open(Hud.Window.BACKPACK)) {
			ctx.sleep(400);
			return pouch.valid() && ctx.backpack.scroll(pouch) && pouch.interact("Summon");
		}

		return false;
	}
}
