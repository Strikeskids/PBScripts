package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.IChat;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.OpenHouse;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.LocationAttribute;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ChatOption;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.GameObject;

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
	public boolean valid() {
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
		if (script.houseTask.isInHouse() || ctx.players.local().inCombat()) {
			return;
		}

		for (GameObject portal : ctx.objects.select().id(ID_ENTER_PORTAL).nearest().first()) {
			final OpenHouse openHouse = script.houseHandler.getOpenHouse();
			if (options.useOtherHouse.get() && openHouse == null) {
				options.status = "Waiting for a house";
				Condition.sleep(500);
				return;
			}

			options.status = "Enter the portal";

			if (ctx.chat.select().text("Go to a friend's house.").isEmpty()) {
				if (options.useOtherHouse.get() && ctx.chat.isInputWidgetOpen()) {
					script.log.info("Try to enter house at \"" + openHouse.getPlayerName() + "\"");
					Component previous = ctx.widgets.component(IChat.WIDGET_INPUT, 1).component(0);
					if (previous.valid() && previous.visible() && previous.text().toLowerCase().equals("last name entered: " + openHouse.getPlayerNameClean())) {
						if (previous.interact("Use:")) {
							enteringHouse.set(true);
						} else {
							return;
						}
					} else {
						Condition.sleep(500);
						enteringHouse.set(ctx.chat.sendInput(openHouse.getPlayerNameClean()));
					}
				} else {
					if (ctx.camera.prepare(portal) && portal.interact("Enter", "Portal")) {
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.chat.select().text("Go to a friend's house.").isEmpty();
							}
						}, 200, 20)) {
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
							Condition.sleep(200);
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
				}, 200, 15)) {
					if (enteringHouse.get()) {
						options.status = "Entered house";
						if (options.useOtherHouse.get() && openHouse != null) {
							openHouse.setEntered();
						}
					}
				}
			}
		}
	}
}
