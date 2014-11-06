package org.logicail.rsbot.scripts.framework.context.rt6.providers;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 19:29
 */
public class ILodestone extends IClientAccessor {
	private final static int HOVER_TELEPORT = 1486;
	private final static int TELEPORT_INTERFACE = 1092;
	private final static int TELEPORT_INTERFACE_HOVERED = 42;
	private final static int TELEPORT_INTERFACE_CHILD = 38;
	private final static int OPEN_TELEPORT_INTERFACE = 1465;
	private final static int OPEN_TELEPORT_INTERFACE_CHILD = 51;
	private static final int SETTING_LODESTONES = 3;
	//private final static int[] TELEPORT_ANIMATIONS = {16385, 16386, 16393};

	public ILodestone(IClientContext context) {
		super(context);
	}


	public boolean teleport(Lodestone lodestone) {
		return teleport(lodestone, false);
	}

	public boolean teleport(Lodestone lodestone, boolean key) {
		return teleport(lodestone, false, key);
	}

	public boolean teleport(final Lodestone lodestone, boolean interruptible, boolean key) {
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
			final Component mapButton = getMapButton();
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
			if (key) {
				interacted = ctx.input.send(lodestone.key);
			} else {
				final Component lodestoneComponent = lodestone.getComponent(ctx);
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						// Avoid overlapping components
						if (!isHovered(lodestone)) {
							lodestoneComponent.hover();
							if (!isHovered(lodestone)) {
								ctx.controller.script().log.info("Failed to hover " + lodestone + " trying key");
								return false;
							}
						}
						return ctx.menu.indexOf(org.powerbot.script.rt6.Menu.filter("Teleport")) > -1;
					}
				}, 150, 7)) {
					ctx.sleep(300);
					if (ctx.menu.click(org.powerbot.script.rt6.Menu.filter("Teleport"))) {
						interacted = true;
					}
				} else {
					// Try key
					interacted = ctx.input.send(lodestone.key);
				}
			}
		}

		return interacted && (interruptible || Condition.wait(new TeleportCondition(ctx, lodestone), 250, 75));
	}

	private boolean isHovered(Lodestone lodestone) {
		for (Component component : ctx.widgets.widget(HOVER_TELEPORT).components()) {
			if (component.childrenCount() > 0) {
				for (Component child : component.components()) {
					if (child.text().toLowerCase().startsWith(lodestone.name().toLowerCase().replace('_', ' '))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private Component getMapButton() {
		return ctx.widgets.component(OPEN_TELEPORT_INTERFACE, OPEN_TELEPORT_INTERFACE_CHILD);
	}

	private boolean isOpen() {
		return ctx.widgets.component(TELEPORT_INTERFACE, TELEPORT_INTERFACE_CHILD).valid();
	}

	// Note order important for ordinal usage
	public static enum Lodestone {
		// Quest settings
		BANDIT_CAMP(new Tile(3214, 2954, 0), "{VK_ALT down}{VK_B}{VK_ALT up}"),
		LUNAR_ISLE(new Tile(2085, 3914, 0), "{VK_ALT down}{VK_L}{VK_ALT up}"),

		// Setting 3
		AL_KHARID(new Tile(3297, 3184, 0), "{VK_A}"),
		ARDOUGNE(new Tile(2634, 3348, 0), "{VK_ALT down}{VK_A}{VK_ALT up}"),
		BURTHORPE(new Tile(2899, 3544, 0), "{VK_B}"),
		CATHERBY(new Tile(2831, 3451, 0), "{VK_C}"),
		DRAYNOR(new Tile(3105, 3298, 0), "{VK_D}"),
		EDGEVILLE(new Tile(3067, 3505, 0), "{VK_E}"),
		FALADOR(new Tile(2967, 3403, 0), "{VK_F}"),
		LUMBRIDGE(new Tile(3233, 3221, 0), "{VK_L}"),
		PORT_SARIM(new Tile(3011, 3215, 0), "{VK_P}"),
		SEERS_VILLAGE(new Tile(2689, 3482, 0), "{VK_S}"),
		TAVERLEY(new Tile(2689, 3482, 0), "{VK_T}"),
		VARROCK(new Tile(3214, 3376, 0), "{VK_V}"),
		YANILlE(new Tile(2529, 3094, 0), "{VK_Y}"),
		CANIFIS(new Tile(3518, 3517, 0), "{VK_ALT down}{VK_C}{VK_ALT up}"),
		EAGLES_PEEK(new Tile(2366, 3479, 0), "{VK_ALT down}{VK_E}{VK_ALT up}"),
		FREMENIK_PROVINCE(new Tile(2711, 3678, 0), "{VK_ALT down}{VK_F}{VK_ALT up}"),
		KARAMJA(new Tile(2761, 3148, 0), "{VK_K}"),
		OOGLOG(new Tile(2533, 2871, 0), "{VK_O}"),
		TIRANNWN(new Tile(2254, 3150, 0), "{VK_ALT down}{VK_T}{VK_ALT up}"),
		WILDERNESS_VOLCANO(new Tile(3142, 3636, 0), "{VK_W}");

		private final Tile location;
		private final String key;
		private final int shift;

		Lodestone(final Tile location, final String key) {
			this.location = location;
			this.key = key;
			this.shift = ordinal() - 2;
		}

		Component getComponent(ClientContext ctx) {
			switch (this) {
				case BANDIT_CAMP:
					return ctx.widgets.component(TELEPORT_INTERFACE, 9);
				case LUNAR_ISLE:
					return ctx.widgets.component(TELEPORT_INTERFACE, 15);
				default:
					return ctx.widgets.component(TELEPORT_INTERFACE, 14 + ordinal());
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
