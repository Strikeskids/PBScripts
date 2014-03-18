package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.Menu;
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
	public static final int WIDGET_STORE = 671;
	public static final int WIDGET_STORE_CLOSE_BUTTON = 21;
	public static final int WIDGET_STORE_CLOSE_BUTTON_CHILD = 1;
	public static final int WIDGET_ORB = 1430;
	public static final int WIDGET_ORB_BUTTON = 109;
	protected final IMethodContext ctx;
	private final IItemStore familiar;
	private final IItemStore backpack;

	public ISummoning(IMethodContext context) {
		super(context);
		ctx = context;
		familiar = new IItemStore(context, ctx.widgets.get(671, 25));
		backpack = new IItemStore(context, ctx.widgets.get(671, 30));
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
		return ctx.widgets.get(WIDGET_STORE, WIDGET_STORE_CLOSE_BUTTON).getChild(WIDGET_STORE_CLOSE_BUTTON_CHILD);
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
			int spaceLeft = getFamiliar().getBoBSpace() - familiar.select().count();
			if (spaceLeft == 0) {
				return false;
			}

			final int backpackCount = backpack.select().id(id).count();
			final Item item = backpack.shuffle().poll();
			final String action = getStoreString(amount);
			if (action.equals("Store-X")) {
				if (item.interact(new Filter<Menu.Entry>() {
					@Override
					public boolean accept(Menu.Entry entry) {
						return entry.action.startsWith(action) && entry.option.startsWith(item.getName());
					}
				}) && ctx.chat.waitForInputWidget()) {
					sleep(800, 1200);
					if (amount == 0) {
						amount = Random.nextInt(backpackCount, (int) (backpackCount * Random.nextDouble(1.0, 5.0)));
					}
					if (ctx.chat.isInputWidgetOpen() && ctx.keyboard.sendln(amount + "")) {
						return true;
					}
				}
			}
			return item.interact(new Filter<Menu.Entry>() {
				@Override
				public boolean accept(Menu.Entry entry) {
					return entry.action.startsWith(action) && entry.option.startsWith(item.getName());
				}
			});
		}

		return false;
	}

	public boolean open() {
		if (isOpen()) {
			return true;
		}

		if (isFamiliarSummoned() && select(Option.INTERACT)) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !ctx.chat.select().text("Store").isEmpty();
				}
			})) {
				for (ChatOption option : ctx.chat.first()) {
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

	public boolean select(Option option) {
		if (option == null) {
			return false;
		}

		closeBankIfInTheWay();

		return super.select(option);
	}

	private void closeBankIfInTheWay() {
		final Component bank = ctx.bank.getWidget(); // 54, 48
		if (bank.isValid()) {
			final Component orb = ctx.widgets.get(WIDGET_ORB, WIDGET_ORB_BUTTON);
			if (bank.getBoundingRect().intersects(orb.getBoundingRect())) {
				sleep(50, 250);
				ctx.bank.close();
				sleep(200, 1000);
			}
		}
	}

	@Override
	public boolean dismissFamiliar() {
		return isFamiliarSummoned() && select(Option.DISMISS);
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
	public boolean renewFamiliar() {
		return select(Option.RENEW_FAMILIAR);
	}

	public boolean canSummon(Familiar familiar) {
		return familiar != null
				&& getSummoningPoints() >= familiar.getRequiredPoints()
				&& (!isFamiliarSummoned() || getTimeLeft() <= 150)
				&& !ctx.backpack.select().id(familiar.getPouchId()).isEmpty();
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
		if (ctx.bank.isOpen()) {
			ctx.bank.close();
			sleep(400, 1200);
		}

		final Item pouch = ctx.backpack.shuffle().poll();
		if (ctx.hud.view(Hud.Window.BACKPACK)) {
			sleep(200, 800);
			return pouch.isValid() && pouch.interact("Summon");
		}

		return false;
	}
}
