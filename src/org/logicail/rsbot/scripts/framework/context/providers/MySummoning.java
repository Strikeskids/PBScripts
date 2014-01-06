package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
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
public class MySummoning extends Summoning {
	// TODO
	public static final int WIDGET_STORE = 671;
	public static final int WIDGET_STORE_CLOSE_BUTTON = 13;
	public static final int WIDGET_ORB = 1430;
	public static final int WIDGET_ORB_BUTTON = 5;

	protected LogicailMethodContext ctx;

	private final MySummoningHelper store;

	public MySummoning(LogicailMethodContext context) {
		super(context);
		ctx = context;
		store = new MySummoningHelper(context);
	}

	public MySummoningHelper getStore() {
		return store;
	}

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
		return ctx.widgets.get(WIDGET_STORE, WIDGET_STORE_CLOSE_BUTTON);
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
			int spaceLeft = getFamiliar().getBoBSpace() - store.select().count();
			if (spaceLeft == 0) {
				return false;
			}

			// TODO: Check might be like bank seperate
			if (ctx.hud.view(Hud.Window.BACKPACK)) {
				for (Item item : ctx.backpack.select().id(id).first()) {
					String action = "Store-" + amount;
					final int backpackCount = ctx.backpack.select().id(id).count();
					if (backpackCount < amount || amount == 0 || backpackCount < spaceLeft) {
						action = "Store-All";
					}
					if (hasAction(item, action)) {
						if (item.interact(action)) {
							return true;
						}
					} else if (item.interact("Store-X") && ctx.chat.waitForInputWidget()) {
						sleep(200, 800);
						ctx.keyboard.sendln(String.valueOf(amount));
					}
				}
			}
		}

		return false;
	}

	public boolean hasAction(final Item item, final String action) {
		final String[] actions = item.getActions();
		if (actions != null) {
			for (final String a : actions) {
				if (a != null && a.matches("^" + action + "(<.*>)?$")) {
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

		if (isFamiliarSummoned() && interactOrb("Store")) {
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
		final Component orb = getOrb();
		return orb.isValid() && orb.isVisible() && orb.interact(action);
	}

	public Component getOrb() {
		return ctx.widgets.get(WIDGET_ORB, WIDGET_ORB_BUTTON);
	}
}
