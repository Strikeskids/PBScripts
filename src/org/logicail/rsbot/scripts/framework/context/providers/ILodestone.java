package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
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
	private final static int TELEPORT_INTERFACE_HOVERED = 61;
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

		return interacted && (interruptible || Condition.wait(new TeleportCondition(ctx, lodestone), Random.nextInt(400, 600), Random.nextInt(33, 40)));
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
		BANDIT_CAMP(new Tile(3214, 2954, 0), 2151, 0, 0x7fff, 15, 7),
		LUNAR_ISLE(new Tile(2085, 3914, 0), 2253, 0, 0xfffff, 190, 61),

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
		private final int setting;
		private final int shift;
		private final int mask;
		private final int component;
		private final int unlockedValue;

		Lodestone(Tile location) {
			this.location = location;
			this.setting = ILodestone.SETTING_LODESTONES;
			this.shift = ordinal() - 2;
			this.mask = 1;
			this.component = 38 + ordinal();
			this.unlockedValue = 1;
		}

		Lodestone(Tile location, int setting, int shift, int mask, int unlockedValue, int component) {
			this.location = location;
			this.setting = setting;
			this.shift = shift;
			this.mask = mask;
			this.component = component;
			this.unlockedValue = unlockedValue;
		}

		Component getComponent(MethodContext ctx) {
			return ctx.widgets.get(TELEPORT_INTERFACE, component);
		}

		public Tile getLocation() {
			return location;
		}

		public boolean isUnlocked(MethodContext ctx) {
			return ctx.settings.get(setting, shift, mask) == unlockedValue;
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
			return local != null && local.getAnimation() == -1 && ctx.movement.getDistance(local, lodestone.getLocation()) < 10;
		}
	}
}
