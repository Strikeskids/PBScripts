package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar.Room;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house.HousePortal;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house.HouseTablet;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house.HouseTeleportStaff;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house.RunicStaff;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.taverley.TaverleyLodestone;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.yanille.YanilleLodestone;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.yanille.YanillePortalWalk;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.logicail.rsbot.util.TargetableRectangle;
import org.powerbot.script.*;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.Menu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 21:48
 */
public class HouseTask extends Branch<LogGildedAltar> {
	public static final int WIDGET_LOADING_HOUSE = 399;
	public static final int WIDGET_LOADING_HOUSE_CHILD = 1; // There's no place like home...
	public static final int WIDGET_HOUSE_OPTIONS = 1443;
	public static final int WIDGET_HOUSE_OPTIONS_HOUSE = 1;
	public static final int WIDGET_HOUSE_OPTIONS_PORTAL = 3;
	public static final int SETTING_HOUSE_LOCATION = 481;
	private static final int EXIT_PORTAL = 13405;
	protected final LogGildedAltarOptions options;

	public HouseTask(LogGildedAltar script, List<Path> houseNodes) {
		super(script);
		options = script.options;

		add(script.housePortal);

		for (Path path : houseNodes) {
			add(createPath(path));
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
	public String toString() {
		return "";
	}

	@Override
	public boolean branch() {
		return !options.banking.get()
				&& !isInHouse()
				&& !ctx.bank.opened();
	}

	public boolean isInHouse() {
		return ctx.game.clientState() == Game.INDEX_MAP_LOADED && (isLoadingHouse() || !ctx.objects.select().id(EXIT_PORTAL).isEmpty());
	}

	public boolean isLoadingHouse() {
		return ctx.widgets.component(WIDGET_LOADING_HOUSE, WIDGET_LOADING_HOUSE_CHILD).valid();
	}

	public HouseLocation getHouseLocation() {
		switch (ctx.varpbits.varpbit(SETTING_HOUSE_LOCATION) & 7) {
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
		return ctx.widgets.widget(WIDGET_HOUSE_OPTIONS);
	}

	public void leaveHouse() {
		for (GameObject portal : ctx.objects.select().id(13405).nearest().first()) {
			script.log.info("Leaving house");

			List<Tile> portalBounds = getTilesAround(portal);

			if (portalBounds.isEmpty()) {
				return;
			}

			final Tile destination = portalBounds.get(Random.nextInt(0, portalBounds.size()));

			if (script.roomStorage.getIndex(ctx.players.local()) != script.roomStorage.getIndex(portal)) {
				final HousePath pathToPortal = new Astar(script).findRoute(portal);

				if (pathToPortal != null && !pathToPortal.traverse(destination)) {
					if (!destination.matrix(ctx).reachable()) {
						script.log.info("Failed to get to portal - trying failsafe");
						final Room current = script.roomStorage.getRoom(ctx.players.local());
						final Room end = script.roomStorage.getRoom(portal);
						GameObject door = ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(current, end)).shuffle().poll();
						door.bounds(script.roomStorage.getRoom(door).getEastWestDoorArea().contains(door) ? DoorOpener.DOOR_BOUNDS_EW : DoorOpener.DOOR_BOUNDS_NS);
						if (DoorOpener.open(ctx, door)) {
							ctx.sleep(300);
						}
					}
				}

				if (ctx.movement.findPath(destination).traverse()) {
					ctx.sleep(300);
				}

				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						final Tile dest = ctx.movement.destination();
						return dest == null || !dest.matrix(ctx).onMap() || dest.distanceTo(ctx.players.local()) <= 4;
					}
				}, 600, 8);
			}


			if (destination.distanceTo(ctx.players.local()) < 20) {
				if (ctx.camera.prepare(portal) && (destination.matrix(ctx).reachable() || script.roomStorage.getIndex(ctx.players.local()) == script.roomStorage.getIndex(portal)) && portal.interact("Enter", "Portal")) {
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

		Area objectArea = object.area();
		if (objectArea == null) {
			objectArea = new Area(object.tile().derive(-1, 1), object.tile().derive(1, -1));
		}

		final Room room = script.roomStorage.getRoom(object);
		if (room != null) {
			final Filter<GameObject> filter = new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					final GameObject.Type type = gameObject.type();
					return type == GameObject.Type.BOUNDARY || type == GameObject.Type.INTERACTIVE;
				}
			};
			for (Tile tile : room.getArea().getTileArray()) {
				if (!objectArea.contains(tile)/* || tile.getMatrix(ctx).isReachable()*/) {
					if (ctx.movement.distance(object, tile) < 6) {
						if (ctx.objects.select().at(tile).select(filter).isEmpty()) {
							tiles.add(tile);
						}
					}
				}
			}
		}
		return tiles;
	}

	public boolean setHouseTeleportMode() {
		final boolean before = isTeleportInHouse();
		final boolean useOtherHouse = options.useOtherHouse.get();

		if ((useOtherHouse && isTeleportPortal()) || (!useOtherHouse && isTeleportInHouse())) {
			return close();
		}

		if (ctx.hud.open(Hud.Menu.OPTIONS)) {
			final Component gameSettings = ctx.widgets.component(1433, 0);
			final Component component = ctx.widgets.component(WIDGET_HOUSE_OPTIONS, useOtherHouse ? WIDGET_HOUSE_OPTIONS_PORTAL : WIDGET_HOUSE_OPTIONS_HOUSE);
			if (gameSettings.visible()) {
				if (gameSettings.interact("Select")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !gameSettings.visible() && component.valid();
						}
					}, Random.nextInt(150, 250), 10);
				}
			}

			if (component.valid() && component.interact("Toggle")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return before != isTeleportInHouse();
					}
				}, Random.nextInt(150, 250), 10);
				ctx.sleep(350);
			}
		}

		return (useOtherHouse && isTeleportPortal()) || (!useOtherHouse && isTeleportInHouse()) && close();
	}

	public boolean close() {
		final Component child = ctx.widgets.component(1477, 54).component(2);
		if (child.valid() && child.visible()) {
			final Rectangle rect = child.boundingRect();
			if (rect != null) {
				rect.translate(15, 20);
				rect.width /= 2;
				rect.height /= 3;
				TargetableRectangle targetableRectangle = new TargetableRectangle(ctx, rect);
				if (targetableRectangle.hover() && ctx.menu.click(Menu.filter("Close Window"))) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !child.valid();
						}
					}, Random.nextInt(150, 250), 10);
				}
			}
		}

		return !child.valid() && !child.visible();
	}

	public boolean isTeleportInHouse() {
		return ctx.varpbits.varpbit(SETTING_HOUSE_LOCATION, 29, 1) == 0;
	}

	public boolean isTeleportPortal() {
		return !isTeleportInHouse();
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
