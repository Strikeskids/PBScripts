package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 19:29
 */
public class ILodestone extends IMethodProvider {
	private final static int TELEPORT_INTERFACE = 1092;
	private final static int TELEPORT_INTERFACE_HOVERED = 42;
	private final static int TELEPORT_INTERFACE_CHILD = 38;
	private final static int OPEN_TELEPORT_INTERFACE = 1465;
	private final static int OPEN_TELEPORT_INTERFACE_CHILD = 10;
	private static final int SETTING_LODESTONES = 3;
	//private final static int[] TELEPORT_ANIMATIONS = {16385, 16386, 16393};

	public ILodestone(IMethodContext context) {
		super(context);
	}

	public boolean teleport(Lodestone lodestone) {
		return teleport(lodestone, false);
	}

	public boolean teleport(final Lodestone lodestone, boolean interruptible) {
		if (lodestone == null) {
			return false;
		}

		final Tile lodestoneLocation = lodestone.getLocation();
		if (lodestoneLocation.distanceTo(ctx.players.local()) < 10) {
			return true;
		}

		if (!lodestone.isUnlocked(ctx)) {
			//script.log.info("Can't use lodestone " + lodestone);
			return false;
		}

		boolean interacted = false;

		if (!isOpen()) {
			final Component mapButton = getMapButton();
			if (!mapButton.isValid()) {
				return false;
			}

			if (lodestone.isPreviousDestination(ctx)) {
				if (mapButton.interact("Previous destination")) {
					interacted = true;
					sleep(100, 500);
				}
			} else if (mapButton.interact("Teleport")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return isOpen();
					}
				}, Random.nextInt(200, 400), Random.nextInt(5, 10))) {
					sleep(100, 500);
				}
			}
		}

		if (isOpen()) {
			final Component lodestoneComponent = lodestone.getComponent(ctx);
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					// Avoid overlapping components
					if (getHighlightedLodestone() != lodestone) {
						lodestoneComponent.hover();
						return getHighlightedLodestone() == lodestone;
					}
					return ctx.menu.indexOf(Menu.filter("Teleport")) > -1;
				}
			}, Random.nextInt(100, 200), Random.nextInt(5, 10))) {
				sleep(100, 500);
				if (ctx.menu.click(Menu.filter("Teleport"))) {
					interacted = true;
				}
			}
		}

		return interacted && (interruptible || Condition.wait(new TeleportCondition(ctx, lodestone), 500, Random.nextInt(33, 40)));
	}

	private Lodestone getHighlightedLodestone() {
		final Component component = ctx.widgets.get(TELEPORT_INTERFACE, TELEPORT_INTERFACE_HOVERED);
		if (isOpen() && component.isValid() && component.isVisible()) {
			// Find nearest lodestone
			final Point point = component.getAbsoluteLocation();
			final int x = point.x + 5;
			final int y = point.y + 5;
			for (Lodestone lodestone : Lodestone.values()) {
				final Point absoluteLocation = lodestone.getComponent(ctx).getAbsoluteLocation();
				//if (lodestone == Lodestone.EDGEVILLE) {
				//	System.out.println("x == absoluteLocation.x && y == absoluteLocation.y");
				//	System.out.println(x + " == " + absoluteLocation.x + " && " + y + " == " + absoluteLocation.y);
				//}
				if (x == absoluteLocation.x && y == absoluteLocation.y) {
					return lodestone;
				}
			}
		}
		return null;
	}

	private Component getMapButton() {
		return ctx.widgets.get(OPEN_TELEPORT_INTERFACE, OPEN_TELEPORT_INTERFACE_CHILD);
	}

	private boolean isOpen() {
		return ctx.widgets.get(TELEPORT_INTERFACE, TELEPORT_INTERFACE_CHILD).isValid();
	}

	// Note order important for ordinal usage
	public static enum Lodestone {
		// Quest settings
		BANDIT_CAMP(new Tile(3214, 2954, 0)),
		LUNAR_ISLE(new Tile(2085, 3914, 0)),

		// Setting 3
		AL_KHARID(new Tile(3297, 3184, 0)),
		ARDOUGNE(new Tile(2634, 3348, 0)),
		BURTHORPE(new Tile(2899, 3544, 0)),
		CATHERBY(new Tile(2831, 3451, 0)),
		DRAYNOR(new Tile(3105, 3298, 0)),
		EDGEVILLE(new Tile(3067, 3505, 0)),
		FALADOR(new Tile(2967, 3403, 0)),
		LUMBRIDGE(new Tile(3233, 3221, 0)),
		PORT_SARIM(new Tile(3011, 3215, 0)),
		SEERS_VILLAGE(new Tile(2689, 3482, 0)),
		TAVERLEY(new Tile(2689, 3482, 0)),
		VARROCK(new Tile(3214, 3376, 0)),
		YANILlE(new Tile(2529, 3094, 0)),
		CANIFIS(new Tile(3518, 3517, 0)),
		EAGLES_PEEK(new Tile(2366, 3479, 0)),
		FREMENIK_PROVINCE(new Tile(2711, 3678, 0)),
		KARAMJA(new Tile(2761, 3148, 0)),
		OOGLOG(new Tile(2533, 2871, 0)),
		TIRANNWN(new Tile(2254, 3150, 0)),
		WILDERNESS_VOLCANO(new Tile(3142, 3636, 0));

		private final Tile location;
		private final int shift;

		Lodestone(Tile location) {
			this.location = location;
			this.shift = ordinal() - 2;
		}

		Component getComponent(MethodContext ctx) {
			switch (this) {
				case BANDIT_CAMP:
					return ctx.widgets.get(TELEPORT_INTERFACE, 9);
				case LUNAR_ISLE:
					return ctx.widgets.get(TELEPORT_INTERFACE, 20);
				default:
					return ctx.widgets.get(TELEPORT_INTERFACE, 19 + ordinal());
			}
		}

		public Tile getLocation() {
			return location;
		}

		public boolean isUnlocked(MethodContext ctx) {
			switch (this) {
				case BANDIT_CAMP:
					return ctx.settings.get(2151, 0, 0x7fff) == 15;
				case LUNAR_ISLE:
					return ctx.settings.get(2253, 0, 0xfffff) == 190;
				default:
					return ctx.settings.get(ILodestone.SETTING_LODESTONES, shift, 1) == 1;
			}
		}

		public boolean isPreviousDestination(MethodContext ctx) {
			return ctx.settings.get(ILodestone.SETTING_LODESTONES, 26, 0x1f) == ordinal() + 1;
		}
	}

	class TeleportCondition extends MethodProvider implements Callable<Boolean> {
		private final Lodestone lodestone;

		TeleportCondition(MethodContext ctx, Lodestone lodestone) {
			super(ctx);
			this.lodestone = lodestone;
		}

		@Override
		public Boolean call() throws Exception {
			final Player local = ctx.players.local();
			return local != null && local.getAnimation() == -1 && ctx.game.getClientState() == Game.INDEX_MAP_LOADED && lodestone.getLocation().getMatrix(ctx).isOnMap();
		}
	}
}
