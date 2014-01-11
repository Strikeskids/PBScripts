package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HousePortal;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HouseTablet;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HouseTeleportStaff;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.RunicStaff;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.taverley.TaverleyLodestone;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille.YanilleLodestone;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille.YanillePortalWalk;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.logicail.rsbot.util.TargetableRectangle;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.*;
import org.powerbot.script.wrappers.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 21:48
 */
public class HouseTask extends Branch<LogGildedAltar> {
	public static final int WIDGET_LOADING_HOUSE = 399; // There's no place like home...
	public static final int WIDGET_HOUSE_OPTIONS = 1443;
	public static final int WIDGET_HOUSE_OPTIONS_HOUSE = 1;
	public static final int WIDGET_HOUSE_OPTIONS_PORTAL = 3;

	@Override
	public String toString() {
		return "HouseTask";
	}

	public static final int SETTING_HOUSE_LOCATION = 481;
	private static final int EXIT_PORTAL = 13405;
	protected final LogGildedAltarOptions options;

	public HouseTask(LogGildedAltar script, Enumeration<Path> houseNodes) {
		super(script);
		options = script.options;

		add(script.housePortal);
		while (houseNodes.hasMoreElements()) {
			add(createPath(houseNodes.nextElement()));
		}
	}

	private NodePath createPath(Path path) {
		switch (path) {
			case HOME_TABLET:
				return new HouseTablet(script, 8013);
			case HOME_YANILLE_WALK:
				return new YanillePortalWalk(script);
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
				return new TaverleyLodestone(script);
		}
		return null;
	}

	@Override
	public boolean branch() {
		return !options.banking.get()
				&& !isInHouse()
				&& !ctx.bank.isOpen();
	}

	public boolean isInHouse() {
		return ctx.game.getClientState() == Game.INDEX_MAP_LOADED && ctx.game.isLoggedIn() && (isLoadingHouse() || !ctx.objects.select().id(EXIT_PORTAL).isEmpty());
	}

	public boolean isLoadingHouse() {
		return ctx.widgets.get(WIDGET_LOADING_HOUSE).isValid();
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

	public void leaveHouse() {
		for (GameObject portal : ctx.objects.select().id(13405).nearest().first()) {
			ctx.log.info("Leaving house");

			List<Tile> portalBounds = getTilesAround(portal);

			if (portalBounds.isEmpty()) {
				return;
			}

			final Tile destination = portalBounds.get(Random.nextInt(0, portalBounds.size()));

			if (script.roomStorage.getIndex(ctx.players.local()) != script.roomStorage.getIndex(portal)) {
				final HousePath pathToPortal = new Astar(script).findRoute(portal);

				if (pathToPortal != null && !pathToPortal.traverse(destination)) {
					if (!destination.getMatrix(ctx).isReachable()) {
						ctx.log.info("Failed to get to portal - trying failsafe");
						final Room current = script.roomStorage.getRoom(ctx.players.local());
						final Room end = script.roomStorage.getRoom(portal);
						for (GameObject door : ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(current, end)).shuffle().first()) {
							if (DoorOpener.open(ctx, door)) {
								sleep(100, 600);
							}
						}
					}

					if (ctx.movement.findPath(destination).traverse()) {
						sleep(150, 500);
					}

					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							final Tile dest = ctx.movement.getDestination();
							return dest == null || !dest.getMatrix(ctx).isOnMap() || dest.distanceTo(ctx.players.local()) <= 4;
						}
					}, 600, 8);
				}
			}

			if (destination.distanceTo(ctx.players.local()) < 20) {
				if (ctx.camera.prepare(portal) && (destination.getMatrix(ctx).isReachable() || script.roomStorage.getIndex(ctx.players.local()) == script.roomStorage.getIndex(portal)) && portal.interact("Enter", "Portal")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !script.houseTask.isInHouse();
						}
					});
				}
			}
		}
	}

	private List<Tile> getTilesAround(GameObject object) {
		List<Tile> tiles = new ArrayList<Tile>();

		Area objectArea = object.getArea();
		if (objectArea == null) {
			objectArea = new Area(object.getLocation().derive(-1, 1), object.getLocation().derive(1, -1));
		}

		final Room room = script.roomStorage.getRoom(object);
		if (room != null) {
			final Filter<GameObject> filter = new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					final GameObject.Type type = gameObject.getType();
					return type == GameObject.Type.BOUNDARY || type == GameObject.Type.INTERACTIVE;
				}
			};
			for (Tile tile : room.getArea().getTileArray()) {
				if (!objectArea.contains(tile)/* || tile.getMatrix(ctx).isReachable()*/) {
					if (ctx.movement.getDistance(object, tile) < 6) {
						if (ctx.objects.select().at(tile).select(filter).isEmpty()) {
							tiles.add(tile);
						}
					}
				}
			}
		}
		return tiles;
	}

	public void setHouseTeleportMode() {
		final boolean before = isTeleportInHouse();
		if ((options.useOtherHouse && !before) || (!options.useOtherHouse && !before)) {
			close();
			return;
		}

		if (ctx.hud.open(Hud.Menu.OPTIONS)) {
			final Component gameSettings = ctx.widgets.get(1433, 0);
			final Component component = ctx.widgets.get(WIDGET_HOUSE_OPTIONS, options.useOtherHouse ? WIDGET_HOUSE_OPTIONS_PORTAL : WIDGET_HOUSE_OPTIONS_PORTAL);
			if (gameSettings.isVisible()) {
				gameSettings.interact("Select");
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !gameSettings.isVisible() && component.isValid();
					}
				});
			}

			if (component.isValid() && component.interact("Select")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return before != isTeleportInHouse();
					}
				}, Random.nextInt(150, 250), 10);
			}

			close();
		}
	}

	private void close() {
		final Component child = ctx.widgets.get(1477, 54).getChild(2);
		if (child.isValid() && child.isVisible()) {
			final Rectangle rect = child.getBoundingRect();
			if (rect != null) {
				rect.translate(15, 20);
				rect.width /= 2;
				rect.height /= 3;
				TargetableRectangle targetableRectangle = new TargetableRectangle(ctx, rect);
				if (targetableRectangle.interact("Close Window")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !child.isValid();
						}
					}, Random.nextInt(150, 250), 10);
				}
			}
		}
	}

	public boolean isTeleportInHouse() {
		return ctx.settings.get(SETTING_HOUSE_LOCATION, 29, 1) == 0;
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
