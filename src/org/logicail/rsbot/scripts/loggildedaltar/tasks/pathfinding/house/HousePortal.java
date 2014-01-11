package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house;

import org.logicail.rsbot.scripts.framework.context.providers.IChat;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.OpenHouse;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LocationAttribute;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 18:46
 */
// implements MessageListener
public class HousePortal extends NodePath/* implements MessageListener*/ {
	public static final int ID_ENTER_PORTAL = 15531;
	private static final LocationAttribute[] PORTAL_LOCATION_ATTRIBUTES = {LocationAttribute.YANILLE_HOUSE, LocationAttribute.RIMMINGTON_HOUSE, LocationAttribute.TAVERLEY_HOUSE, LocationAttribute.POLLNIVNEACH_HOUSE, LocationAttribute.RELLEKKA_HOUSE, LocationAttribute.BRIMHAVEN_HOUSE};

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
			sleep(200, 800);
		} else if (locationAttribute.isInLargeArea(ctx)) {
			// never gonna happen , getPortalLocation only if in area
			doSmall();
			if (ctx.players.local().isInMotion()) {
				sleep(300, 1600);
			}
		}
	}

	@Override
	protected boolean doSmall() {
		if (LocationAttribute.YANILLE_HOUSE.isInLargeArea(ctx)) {
			final Tile location = ctx.players.local().getLocation();
			if (location.x < 2539 && location.y > 3072 && location.y < 3112) {
				BasicNamedQuery<GameObject> door = script.yanilleLodestone.nextDoor();
				if (!door.isEmpty()) {
					door.each(new DoorOpener(ctx));
					door = script.yanilleLodestone.nextDoor();
				}

				if (door.isEmpty()) {
					enterPortal();
				}
			}
		}

		return super.doSmall();
	}

	/**
	 * Enter house portal
	 *
	 * @return How long to delay before next call or -1 on stop
	 */
	public void enterPortal() {
		boolean entering = false;
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
					ctx.log.info("Try to enter house at \"" + openHouse.getPlayerName() + "\"");
					Component previous = ctx.widgets.get(IChat.WIDGET_INPUT, 3).getChild(0);
					if (previous.isValid() && previous.isOnScreen() && previous.getText().toLowerCase().equals("last name entered: " + openHouse.getPlayerName()) && previous.interact("Use:")) {
						entering = true;
					} else {
						sleep(150, 850);
						entering = makeInput(openHouse.getPlayerName());
					}
				} else {
					if (ctx.camera.prepare(portal) && portal.interact("Enter", "Portal")) {
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.chat.select().text("Go to a friend's house.").isEmpty();
							}
						})) {
							sleep(100, 650);
							return; // recall enterPortal
						}
					}
				}
			} else {
				// Chat open
				if (options.useOtherHouse.get()) {
					for (final ChatOption option : ctx.chat.first()) {
						if (option.select(Random.nextBoolean())) {
							if (ctx.chat.waitForInputWidget()) {
								sleep(50, 550);
								return;
							}
						}
					}
				} else {
					for (ChatOption option : ctx.chat.select()) {
						ctx.log.info(option.getText());
					}

					for (ChatOption option : ctx.chat.select().text("Go to your house.").first()) {
						entering = option.select(Random.nextBoolean());
					}
				}
			}

			if (entering) {
				options.status = "Waiting for house to load";

				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return script.houseTask.isInHouse() && script.houseTask.isLoadingHouse();
					}
				})) {
					options.status = "Entered house";
					if (options.useOtherHouse.get() && openHouse != null) {
						openHouse.setEntered();
					}
				}

				sleep(400, 1200);
			}
		}
	}

	private boolean makeInput(String input) {
		final Component textBox = ctx.chat.getInputTextBox();
		if (textBox.isOnScreen()) {
			String text = textBox.getText();
			if (text == null || !text.equalsIgnoreCase(input)) {
				if (text != null) {
					for (int i = 0; i <= text.length(); ++i) {
						ctx.keyboard.send("{VK_BACK_SPACE down}");
						ctx.keyboard.send("{VK_BACK_SPACE up}");
					}
				}
				ctx.keyboard.send(input);
				text = textBox.getText();
			}
			if (text != null && text.equalsIgnoreCase(text) && textBox.isOnScreen()) {
				return ctx.keyboard.send("", true);
			}
		}

		return false;
	}

	/*@Override
	public void messaged(MessageEvent messageEvent) {
		if (entering) {
			String message = messageEvent.getMessage();
			if ((messageEvent.getId() == 0
					&& (message.equals("They have locked their house to visitors.") || message.equals("They do not seem to be at home.")))
					|| (messageEvent.getId() == 4 && message.equals("That player is offline, or has privacy mode enabled."))) {
				entering = false;
			}
		}
	}*/
}
