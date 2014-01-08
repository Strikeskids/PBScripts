package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 19:29
 */
public class Lodestones extends LogicailMethodProvider {
	private final static int TELEPORT_INTERFACE = 1092;
	private final static int TELEPORT_INTERFACE_CHILD = 38;
	private final static int OPEN_TELEPORT_INTERFACE = 1465;
	private final static int OPEN_TELEPORT_INTERFACE_CHILD = 10;
	private final static int[] TELEPORT_ANIMATIONS = {16385, 16386, 16393};
	private final static int SETTING_LODESTONES = 3;

	public Lodestones(LogicailMethodContext context) {
		super(context);
	}

	public boolean teleport(Lodestone lodestone) {
		final Tile location = lodestone.getLocation();
		if (location.distanceTo(ctx.players.local()) < 10) {
			ctx.movement.stepTowards(location.randomize(2, 2));
			return true;
		}

		if (!lodestone.isUnlocked(ctx)) {
			ctx.log.info("Can't use lodestone " + lodestone);
			return false;
		}

		if (!isOpen()) {
			final Component button = getButton();
			if (!button.isValid()) {
				return false;
			}

			if (isPreviousDestination(lodestone)) {
				if (button.interact("Previous-destination")) {
					return waitForTeleport(lodestone);
				}
			} else if (button.interact("Home-teleport")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return isOpen();
					}
				});
			}
		}

		if (isOpen()) {
			final Component component = lodestone.getComponent(ctx);
			if (component.isValid() && component.interact("Teleport")) {
				return waitForTeleport(lodestone);
			}
		}

		return false;
	}

	public Component getButton() {
		return ctx.widgets.get(OPEN_TELEPORT_INTERFACE, OPEN_TELEPORT_INTERFACE_CHILD);
	}

	public boolean isOpen() {
		return ctx.widgets.get(TELEPORT_INTERFACE, TELEPORT_INTERFACE_CHILD).isValid();
	}

	public boolean isPreviousDestination(Lodestone lodestone) {
		return ctx.settings.get(SETTING_LODESTONES, 26, 0x1f) == lodestone.ordinal();
	}

	private boolean waitForTeleport(final Lodestone lodestone) {
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final Player local = ctx.players.local();
				return local != null && local.getAnimation() == -1 && ctx.movement.getDistance(local, lodestone.getLocation()) < 10;
			}
		});
	}

	public static enum Lodestone {
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
		WILDERNESS_VOLCANO(new Tile(3142, 3636, 0)),

		// Quest settings
		BANDIT_CAMP(new Tile(3214, 2954, 0), 2151, 0, 0x7fff, 15, 7),
		LUNAR_ISLE(new Tile(2085, 3914, 0), 2253, 0, 0xfffff, 190, 61);

		private final Tile location;
		private final int setting;
		private final int shift;
		private final int mask;
		private final int component;
		private final int unlockedValue;

		Lodestone(Tile location) {
			this.location = location;
			this.setting = 3;
			this.shift = ordinal();
			this.mask = 1;
			this.component = 40 + ordinal();
			unlockedValue = 1;
		}

		Lodestone(Tile location, int setting, int shift, int mask, int unlockedValue, int component) {
			this.location = location;
			this.setting = setting;
			this.shift = shift;
			this.mask = mask;
			this.component = component;
			this.unlockedValue = unlockedValue;
		}

		public Component getComponent(MethodContext ctx) {
			return ctx.widgets.get(TELEPORT_INTERFACE, component);
		}

		public Tile getLocation() {
			return location;
		}

		public boolean isUnlocked(MethodContext ctx) {
			return ctx.settings.get(setting, shift, mask) == unlockedValue;
		}
	}
}
