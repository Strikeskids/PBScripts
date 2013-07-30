package org.logicail.scripts.logartisanarmourer.tasks.swords;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.tasks.Anvil;
import org.logicail.scripts.logartisanarmourer.wrapper.HitType;
import org.logicail.scripts.logartisanarmourer.wrapper.Sword;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:37
 */
public class MakeSword extends Node {
	public static final int WIDGET_SWORD_INTERFACE = 1074;
	public static final int WIDGET_SWORD_COOLDOWN = 16;
	public static final int[] HEATED_INGOTS = {20566, 20567, 20568, 20569, 20570, 20571};
	public static final int TONGS = 20565;
	public static final int[] SWORD_PLANS = {20559, 20560, 20561, 20562, 20563, 20564};
	private final LogArtisanArmourerOptions options;
	Anvil anvilHelper;

	public MakeSword(LogicailMethodContext ctx, LogArtisanArmourerOptions options) {
		super(ctx);
		this.options = options;
		anvilHelper = new Anvil(ctx, options);
	}

	public static int getCooldown(LogicailMethodContext ctx) {
		if (!isOpen(ctx)) {
			return Integer.MAX_VALUE;
		}

		return Integer.parseInt(ctx.widgets.get(WIDGET_SWORD_INTERFACE, WIDGET_SWORD_COOLDOWN).getText());

	}

	public static boolean isOpen(LogicailMethodContext ctx) {
		return ctx.widgets.get(WIDGET_SWORD_INTERFACE, WIDGET_SWORD_COOLDOWN).isValid();
	}

	public int getCooldown() {
		return getCooldown(ctx);
	}

	public boolean isOpen() {
		return isOpen(ctx);
	}

	/**
	 * Get sword parts by highest discrepancy first
	 *
	 * @return
	 */
	public Sword[] getSwordPartsByDistance() {
		final ArrayList<Sword> list = new ArrayList<>();
		for (Sword swordPart : Sword.values()) {
			if (swordPart.getHitsNeeded(ctx) > 0) {
				list.add(swordPart);
			}
		}

		if (list.size() > 0) {
			Collections.sort(list, new SwordComparator(ctx));

			HitType hitType = list.get(0).getRequiredHitType(ctx);

			final ArrayList<Sword> newList = new ArrayList<>();

			for (final Sword sword : list) {
				if (sword.getRequiredHitType(ctx) == hitType) {
					newList.add(sword);
				}
			}

/*

			int distance = Integer.MIN_VALUE;
			final ArrayList<Sword> newList = new ArrayList<>();
			for (final Sword sword : list) {
				if (distance == Integer.MIN_VALUE) {
					distance = sword.getHitsNeeded();
					newList.add(sword);
				} else if (distance == sword.getHitsNeeded()) {
					newList.add(sword);
				} else {
					break;
				}
			}
*/
			return newList.toArray(new Sword[newList.size()]);
		}

		return null;
	}

	public boolean closeInterface() {
		if (!isOpen()) {
			return true;
		}

		Component component = ctx.widgets.get(1074, 145);
		if (component.isValid() && component.interact("Close")) {
			ctx.waiting.wait(1000, new Condition() {
				@Override
				public boolean validate() {
					return !isOpen();
				}
			});
		}

		return isOpen();
	}

	@Override
	public boolean activate() {
		return !options.finishedSword
				&& options.gotPlan
				&& !ctx.backpack.select().id(TONGS).isEmpty();
	}

	@Override
	public void execute() {
		if (!isOpen()) {
			anvilHelper.click();
			sleep(100, 600);
			return;
		}

		Sword hitPart = null;

		// Finish tip first
		if (Sword.Eighth.getHitsNeeded(ctx) > 0) {
			hitPart = Sword.Eighth;
		} else if (Sword.Sixteenth.getHitsNeeded(ctx) > 0) {
			hitPart = Sword.Sixteenth;
		} else {
			final Sword[] parts = getSwordPartsByDistance();
			if (parts != null && parts.length > 0) {
/*
                System.out.println("----");

				for (Sword part : parts) {
					System.out.println(part.getRequiredHitType());
				}

				System.out.println("----");
*/
				hitPart = parts[Random.nextInt(0, parts.length)];
			}
		}

		if (hitPart == null || getCooldown() == 0) {
			options.finishedSword = true;
			closeInterface();
			return;
		}

		if (setHitType(hitPart.getRequiredHitType(ctx))) {
			//LogHandler.print("Hit " + hitPart + " with " + hitPart.getRequiredHitType());
			if (hitPart.clickButton(ctx)) {
				sleep(250, 700);
			}
		}
	}

	public HitType getCurrentHitType() {
		return HitType.values()[ctx.settings.get(132, 16, 3)];
	}

	public boolean setHitType(final HitType hitType) {
		if (getCurrentHitType() == hitType) {
			return true;
		}

		Component buttonWidget = ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, hitType.getWidgetButton());
		if (buttonWidget.isValid() && buttonWidget.interact("Select")) {
			ctx.waiting.wait(2000, new Condition() {
				@Override
				public boolean validate() {
					return getCurrentHitType() == hitType;
				}
			});
			sleep(200, 800);
		}

		return getCurrentHitType() == hitType;
	}

	static class SwordComparator implements Comparator<Sword> {
		private LogicailMethodContext context;

		SwordComparator(LogicailMethodContext context) {
			this.context = context;
		}

		@Override
		public int compare(Sword o1, Sword o2) {
			return o2.getHitsNeeded(context) - o1.getHitsNeeded(context);
		}
	}
}

