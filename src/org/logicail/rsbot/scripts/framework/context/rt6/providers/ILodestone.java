package org.logicail.rsbot.scripts.framework.context.rt6.providers;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Game;
import org.powerbot.script.rt6.Player;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 19:29
 */
public class ILodestone extends IClientAccessor {
	private final static int TELEPORT_INTERFACE = 1092;
	private final static int TELEPORT_INTERFACE_HOVERED = 42;
	private final static int TELEPORT_INTERFACE_CHILD = 38;
	private final static int OPEN_TELEPORT_INTERFACE = 1465;
	private final static int OPEN_TELEPORT_INTERFACE_CHILD = 10;
	private static final int SETTING_LODESTONES = 3;
	//private final static int[] TELEPORT_ANIMATIONS = {16385, 16386, 16393};

	public ILodestone(IClientContext context) {
		super(context);
	}

	public boolean teleport(Lodestone lodestone) {
		return teleport(lodestone, false);
	}

	public boolean teleport(final Lodestone lodestone, boolean interruptible) {
		if (lodestone == null) {
			return false;
		}

		final Tile lodestoneLocation = lodestone.tile();
		if (lodestoneLocation.distanceTo(ctx.players.local()) < 10) {
			return true;
		}

		if (!lodestone.isUnlocked(ctx)) {
			//script.log.info("Can't use lodestone " + lodestone);
			return false;
		}

		boolean interacted = false;

		if (!isOpen()) {
			final org.powerbot.script.rt6.Component mapButton = getMapButton();
			if (!mapButton.valid()) {
				return false;
			}

			if (lodestone.isPreviousDestination(ctx)) {
				if (mapButton.interact("Previous destination")) {
					interacted = true;
				}
			} else if (mapButton.interact("Teleport")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return isOpen();
					}
				}, 300, 7);
			}
		}

		if (isOpen()) {
			final org.powerbot.script.rt6.Component lodestoneComponent = lodestone.getComponent(ctx);
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					// Avoid overlapping components
					if (getHighlightedLodestone() != lodestone) {
						lodestoneComponent.hover();
						return getHighlightedLodestone() == lodestone;
					}
					return ctx.menu.indexOf(org.powerbot.script.rt6.Menu.filter("Teleport")) > -1;
				}
			}, 150, 7)) {
				ctx.sleep(300);
				if (ctx.menu.click(org.powerbot.script.rt6.Menu.filter("Teleport"))) {
					interacted = true;
				}
			}
		}

		return interacted && (interruptible || Condition.wait(new TeleportCondition(ctx, lodestone), 250, 75));
	}

	private Lodestone getHighlightedLodestone() {
		final org.powerbot.script.rt6.Component component = ctx.widgets.component(TELEPORT_INTERFACE, TELEPORT_INTERFACE_HOVERED);
		if (isOpen() && component.valid() && component.visible()) {
			// Find nearest lodestone
			final Point point = component.screenPoint();
			final int x = point.x + 5;
			final int y = point.y + 5;
			for (Lodestone lodestone : Lodestone.values()) {
				final Point absoluteLocation = lodestone.getComponent(ctx).screenPoint();
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

	private org.powerbot.script.rt6.Component getMapButton() {
		return ctx.widgets.component(OPEN_TELEPORT_INTERFACE, OPEN_TELEPORT_INTERFACE_CHILD);
	}

	private boolean isOpen() {
		return ctx.widgets.component(TELEPORT_INTERFACE, TELEPORT_INTERFACE_CHILD).valid();
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

		org.powerbot.script.rt6.Component getComponent(ClientContext ctx) {
			switch (this) {
				case BANDIT_CAMP:
					return ctx.widgets.component(TELEPORT_INTERFACE, 9);
				case LUNAR_ISLE:
					return ctx.widgets.component(TELEPORT_INTERFACE, 20);
				default:
					return ctx.widgets.component(TELEPORT_INTERFACE, 19 + ordinal());
			}
		}

		public Tile tile() {
			return location;
		}

		public boolean isUnlocked(ClientContext ctx) {
			switch (this) {
				case BANDIT_CAMP:
					return ctx.varpbits.varpbit(2151, 0, 0x7fff) == 15;
				case LUNAR_ISLE:
					return ctx.varpbits.varpbit(2253, 0, 0xfffff) == 190;
				default:
					return ctx.varpbits.varpbit(ILodestone.SETTING_LODESTONES, shift, 1) == 1;
			}
		}

		public boolean isPreviousDestination(ClientContext ctx) {
			return ctx.varpbits.varpbit(ILodestone.SETTING_LODESTONES, 26, 0x1f) == ordinal() + 1;
		}
	}

	class TeleportCondition extends ClientAccessor implements Callable<Boolean> {
		private final Lodestone lodestone;

		TeleportCondition(ClientContext ctx, Lodestone lodestone) {
			super(ctx);
			this.lodestone = lodestone;
		}

		@Override
		public Boolean call() throws Exception {
			final Player local = ctx.players.local();
			return local != null && local.animation() == -1 && ctx.game.clientState() == Game.INDEX_MAP_LOADED && lodestone.tile().matrix(ctx).onMap();
		}
	}
}
