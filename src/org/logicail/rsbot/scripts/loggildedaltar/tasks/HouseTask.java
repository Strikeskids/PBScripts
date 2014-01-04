package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HousePortal;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HouseTablet;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HouseTeleportStaff;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.RunicStaff;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille.YanilleLodestone;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Widget;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 21:48
 */
public class HouseTask extends Branch<LogGildedAltar> {
	public static final int WIDGET_LOADING_HOUSE = 399; // There's no place like home...
	public static final int WIDGET_HOUSE_OPTIONS = 398;
	public static final int WIDGET_OPTIONS = 261;
	public static final int WIDGET_OPTIONS_BUTTON = 25;
	public static final int WIDGET_BUTTON_BUILDING_MODE_ON = 18;
	public static final int WIDGET_BUTTON_BUILDING_MODE_OFF = 1;
	// "Building mode on"
	public static final int WIDGET_BUTTON_ARRIVE_HOUSE = 28;
	public static final int WIDGET_BUTTON_ARRIVE_PORTAL = 27;
	// "When teleporting, arrive at portal"
	public static final int WIDGET_BUTTON_CLOSE = 21;
	private static final int SETTING_HOUSE = 1189;
	private static final int SETTING_BUILD = 483;
	private static final int SETTING_HOUSE_LOCATION = 481;
	protected final LogGildedAltarOptions options;

	public HouseTask(LogGildedAltar script) {
		super(script);
		options = script.options;
	}

	@Override
	public boolean branch() {
		return !options.banking
				&& !isInHouse()
				&& !ctx.bank.isOpen();
	}

	public boolean isInHouse() {
		return ctx.game.getClientState() == Game.INDEX_MAP_LOADED && ctx.game.isLoggedIn() && (isLoadingHouse() || !ctx.objects.select().id(13405).isEmpty());
	}

	public boolean isLoadingHouse() {
		return ctx.widgets.get(WIDGET_LOADING_HOUSE).isValid();
	}

	public NodePath createPath(Path path) {
		switch (path) {
			case HOME_TABLET:
				return new HouseTablet(script, 8013);
			case HOME_YANILLE_WALK:
				//return new YanillePortalWalk();
			case HOME_PORTAL:
				return new HousePortal(script);
			case YANILLE_LODESTONE:
				return new YanilleLodestone(script);
			case HOME_TELEPORT:
				return new HouseTeleportStaff(script, path, 1385, 1399, 1407, 3053, 3054, 6562, 6563);
			case HOME_TELEPORT_RUNIC_STAFF:
				return new RunicStaff(script);
			case YANILLE_TABLET:
				return new HouseTablet(script, 18814);
			case TAVERLEY_LODESTONE:
				//return new TaverleyLodestone();
		}
		return null;
	}

	public HouseLocation getHouseLocation() {
		switch (ctx.settings.get(SETTING_HOUSE_LOCATION) & 7) {
			case 1:
				return HouseLocation.RIMMINGTON;
			case 2:
				return HouseLocation.TAVERLEY;
			case 3:
				return HouseLocation.POLLNIVNEACH;
			case 4:
				return HouseLocation.RELLEKKA;
			case 5:
				return HouseLocation.BRIMHAVEN;
			case 6:
				return HouseLocation.YANILLE;
		}

		return HouseLocation.YANILLE;
	}

	public Widget getWidget() {
		return ctx.widgets.get(WIDGET_HOUSE_OPTIONS);
	}

	public void setHouseTeleportMode() {
		if (options.useOtherHouse) {
			if (script.houseTask.isTeleportInHouse()) {
				if (script.houseTask.openHouseOptions()) {
					script.houseTask.setTeleportPortal();
				}
			}
		} else if (!script.houseTask.isTeleportInHouse()) {
			if (script.houseTask.openHouseOptions()) {
				script.houseTask.setTeleportHouse();
			}
		}

		script.houseTask.closeHouseOptions();
	}

	public boolean closeHouseOptions() {
		if (!isOpen()) {
			return true;
		}

		Component closeButton = getWidget().getComponent(WIDGET_BUTTON_CLOSE);
		if (closeButton.isValid() && closeButton.interact("Close")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !isOpen();
				}
			});
		}

		return !isOpen();
	}

	public boolean openHouseOptions() {
		if (isOpen()) {
			return true;
		}

		// TODO: Check / see Options.getwindows
		if (ctx.hud.open(Hud.Menu.OPTIONS)) {
			sleep(400, 600);
		}

		Component houseOptionsButton = ctx.widgets.get(WIDGET_OPTIONS, WIDGET_OPTIONS_BUTTON);
		if (houseOptionsButton.isValid() && houseOptionsButton.interact("Open House Options")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return isOpen();
				}
			});
		}

		return isOpen();
	}

	public boolean isOpen() {
		return ctx.widgets.get(WIDGET_HOUSE_OPTIONS).isValid();
	}

	public boolean setTeleportHouse() {
		if (isOpen()) {
			Component houseButton = getWidget().getComponent(WIDGET_BUTTON_ARRIVE_HOUSE);
			if (houseButton.isValid() && houseButton.interact("When teleporting, arrive in house")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return isTeleportInHouse();
					}
				});
			}
		}

		return isTeleportInHouse();
	}

	public boolean isTeleportInHouse() {
		return ctx.settings.get(SETTING_HOUSE_LOCATION, 29, 1) == 0;
	}

	public boolean setTeleportPortal() {
		if (isOpen()) {
			Component houseButton = getWidget().getComponent(WIDGET_BUTTON_ARRIVE_PORTAL);
			if (houseButton.isValid() && houseButton.interact("When teleporting, arrive at portal")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !isTeleportInHouse();
					}
				});
			}
		}
		return !isTeleportInHouse();
	}

	public void leaveHouse() {
		for (GameObject portal : ctx.objects.select().id(13405).nearest().first()) {
			/*ctx.log.info("Leaving house");

			Tile[] portalBounds = getTilesAroundPortal(portal);

			if (portalBounds.length == 0) {
				return;
			}

			final Tile destination = portalBounds[Random.nextInt(0, portalBounds.length)];

			if (HousePath.getIndex(Players.getLocal()) != HousePath.getIndex(portal)) {
				final HousePath pathToPortal = new HousePath(portal);

				if (!pathToPortal.traverse(destination)) {
					if (!destination.canReach()) {
						GildedAltar.get().getLogHandler().print("Failed to get to portal - trying failsafe", Color.RED);
						SceneObject closestDoor = SceneEntities.getNearest(new Filter<SceneObject>() {
							final Area areaStart = pathToPortal.getCurrentRoom().getWallArea();
							final Area areaEnd = pathToPortal.getEnd().getWallArea();

							@Override
							public boolean accept(SceneObject sceneObject) {
								return sceneObject != null
										&& Arrays.binarySearch(HousePath.DOOR_CLOSED, sceneObject.getId()) >= 0
										&& areaStart.contains(sceneObject)
										&& areaEnd.contains(sceneObject);
							}
						});

						if (closestDoor != null) {
							if (Util.openDoor(closestDoor, Random.nextInt(4000, 6000))) {
								Task.sleep(50, 300);
							}
						}
					}

					if (Walking.findPath(destination).traverse()) {
						Task.sleep(150, 400);
					}

					Waiting.waitFor(5000, new Condition() {
						@Override
						public boolean validate() {
							Tile dest = Walking.getDestination();
							return !dest.validate() || Calculations.distanceTo(destination) <= 3;
						}
					});
				}
			}

			if (Calculations.distanceTo(destination) < 20) {
				if (Util.turnTo(portal) && (destination.canReach() || HousePath.getIndex(Players.getLocal()) == HousePath.getIndex(portal)) && portal.interact("Enter", "Portal")) {
					Timer t = new Timer(4000);
					while (t.isRunning()) {
						if (Game.getClientState() == Game.INDEX_MAP_LOADED && !House.isInHouse()) {
							Task.sleep(100, 350);
							break;
						}
						sleep(40, 80);
					}
				}
			}*/
			throw new NotImplementedException();
		}
	}

	public static enum HouseLocation {
		RIMMINGTON,
		TAVERLEY,
		POLLNIVNEACH,
		RELLEKKA,
		BRIMHAVEN,
		YANILLE
	}
}
