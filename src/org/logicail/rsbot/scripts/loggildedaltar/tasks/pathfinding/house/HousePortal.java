package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house;

import org.logicail.rsbot.scripts.framework.context.providers.IChat;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.OpenHouse;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LocationAttribute;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 18:46
 */
public class HousePortal extends NodePath {
	public static final int ID_ENTER_PORTAL = 15531;
	private static final LocationAttribute[] PORTAL_LOCATION_ATTRIBUTES = {LocationAttribute.YANILLE_HOUSE, LocationAttribute.RIMMINGTON_HOUSE, LocationAttribute.TAVERLEY_HOUSE, LocationAttribute.POLLNIVNEACH_HOUSE, LocationAttribute.RELLEKKA_HOUSE, LocationAttribute.BRIMHAVEN_HOUSE};
	public final AtomicBoolean enteringHouse = new AtomicBoolean();

	public HousePortal(LogGildedAltar script) {
		super(script, Path.HOME_PORTAL);
	}

	@Override
	public boolean doLarge() {
		return false;
	}

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		return new ArrayList<BankRequiredItem>();
	}

	@Override
	public boolean isValid() {
		return getPortalLocation() != null;
	}

	public LocationAttribute getPortalLocation() {
		for (LocationAttribute locationAttribute : PORTAL_LOCATION_ATTRIBUTES) {
			if (locationAttribute.isInSmallArea(ctx)) {
				return locationAttribute;
			}
		}

		return null;
	}

	@Override
	public void run() {
		final LocationAttribute locationAttribute = getPortalLocation();
		if (locationAttribute == null) {
			return;
		}

		if (locationAttribute.isInSmallArea(ctx)) {
			enterPortal();
		} else if (locationAttribute.isInLargeArea(ctx)) {
			// never gonna happen , getPortalLocation only if in area
			doSmall();
		}
	}

	@Override
	protected boolean doSmall() {
		if (LocationAttribute.YANILLE_HOUSE.isInLargeArea(ctx)) {
			enterPortal();
		}

		return super.doSmall();
	}

	/**
	 * Enter house portal
	 *
	 * @return How long to delay before next call or -1 on stop
	 */
	public void enterPortal() {
		if (script.houseTask.isInHouse() || ctx.players.local().isInCombat()) {
			return;
		}

		for (GameObject portal : ctx.objects.select().id(ID_ENTER_PORTAL).nearest().first()) {
			final OpenHouse openHouse = script.houseHandler.getOpenHouse();
			if (options.useOtherHouse.get() && openHouse == null) {
				options.status = "Waiting for a house";
				sleep(250, 1250);
				return;
			}

			options.status = "Enter the portal";

			if (ctx.chat.select().text("Go to a friend's house.").isEmpty()) {
				if (options.useOtherHouse.get() && ctx.chat.isInputWidgetOpen()) {
					script.log.info("Try to enter house at \"" + openHouse.getPlayerName() + "\"");
					Component previous = ctx.widgets.get(IChat.WIDGET_INPUT, 1).getChild(0);
					if (previous.isValid() && previous.isVisible() && previous.getText().toLowerCase().equals("last name entered: " + openHouse.getPlayerNameClean())) {
						if (previous.interact("Use:")) {
							enteringHouse.set(true);
						} else {
							return;
						}
					} else {
						sleep(150, 850);
						enteringHouse.set(ctx.chat.sendInput(openHouse.getPlayerNameClean()));
					}
				} else {
					if (ctx.camera.prepare(portal) && portal.interact("Enter", "Portal")) {
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.chat.select().text("Go to a friend's house.").isEmpty();
							}
						})) {
							return; // recall enterPortal
						}
					}
				}
			} else {
				// Chat open
				if (options.useOtherHouse.get()) {
					for (final ChatOption option : ctx.chat.first()) {
						if (option.select(Random.nextBoolean())) {
							ctx.chat.waitForInputWidget();
							sleep(50, 400);
							return;
						}
					}
				} else {
					for (ChatOption option : ctx.chat.select().text("Go to your house.").first()) {
						enteringHouse.set(option.select(Random.nextBoolean()));
					}
				}
			}

			if (enteringHouse.get()) {
				options.status = "Waiting for house to load";

				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !enteringHouse.get() || (script.houseTask.isInHouse() && script.houseTask.isLoadingHouse());
					}
				}, 200, 10)) {
					if (enteringHouse.get()) {
						options.status = "Entered house";
						if (options.useOtherHouse.get() && openHouse != null) {
							openHouse.setEntered();
						}
					}
				}

				sleep(400, 1200);
			}
		}
	}
}
