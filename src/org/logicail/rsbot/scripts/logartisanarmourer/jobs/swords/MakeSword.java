package org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.HitType;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Sword;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/12/12
 * Time: 23:32
 */
public class MakeSword extends ArtisanArmourerTask {
	public static final int WIDGET_SWORD_INTERFACE = 1074;
	public static final int WIDGET_SWORD_COOLDOWN = 16;
	public static final int[] HEATED_INGOTS = {20566, 20567, 20568, 20569, 20570, 20571};
	public static final int TONGS = 20565;
	public static final int[] SWORD_PLANS = {20559, 20560, 20561, 20562, 20563, 20564};
	private SmithAnvil smithAnvil;

	public MakeSword(LogArtisanWorkshop script, SmithAnvil smithAnvil) {
		super(script);
		this.smithAnvil = smithAnvil;
	}

	@Override
	public String toString() {
		return "Make Sword";
	}

	@Override
	public boolean activate() {
		return !options.finishedSword
				&& options.gotPlan
				&& !ctx.backpack.select().id(TONGS).isEmpty();
	}

	@Override
	public void run() {
		if (!isOpen()) {
			smithAnvil.clickAnvil();
			sleep(100, 600);
			return;
		}

		options.status = "Making sword";

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
			//LogArtisanWorkshop.get().getLogHandler().print("No more parts can be hit");
			options.finishedSword = true;
			closeInterface();
			return;
		}

		if (HitType.setHitType(ctx, hitPart.getRequiredHitType(ctx, this))) {
			//LogHandler.print("Hit " + hitPart + " with " + hitPart.getRequiredHitType());
			if (hitPart.clickButton(ctx, this)) {
				sleep(250, 700);
			}
		}
	}

	public int getCooldown() {
		if (!isOpen()) {
			return Integer.MAX_VALUE;
		}

		return Integer.parseInt(ctx.widgets.get(WIDGET_SWORD_INTERFACE, WIDGET_SWORD_COOLDOWN).getText());
	}

	public boolean isOpen() {
		return ctx.widgets.get(WIDGET_SWORD_INTERFACE, WIDGET_SWORD_COOLDOWN).isValid();
	}

	/**
	 * Get sword parts by highest discrepancy first
	 *
	 * @return
	 */
	public Sword[] getSwordPartsByDistance() {
		final ArrayList<Sword> list = new ArrayList<Sword>();
		for (Sword swordPart : Sword.values()) {
			if (swordPart.getHitsNeeded(ctx) > 0) {
				list.add(swordPart);
			}
		}

		if (list.size() > 0) {
			Collections.sort(list, new SwordComparator(ctx));

			HitType hitType = list.get(0).getRequiredHitType(ctx, this);

			final List<Sword> newList = new LinkedList<Sword>();

			for (final Sword sword : list) {
				if (sword.getRequiredHitType(ctx, this) == hitType) {
					newList.add(sword);
				}
			}

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
			org.powerbot.script.util.Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !isOpen();
				}
			});
		}

		return isOpen();
	}

	static class SwordComparator extends LogicailMethodProvider implements Comparator<Sword> {
		SwordComparator(LogicailMethodContext context) {
			super(context);
		}

		@Override
		public int compare(Sword o1, Sword o2) {
			return o2.getHitsNeeded(ctx) - o1.getHitsNeeded(ctx);
		}
	}
}
