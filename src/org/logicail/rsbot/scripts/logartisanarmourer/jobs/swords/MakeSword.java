package org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.HitType;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Sword;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.Component;

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
	private final SmithAnvil smithAnvil;

	public MakeSword(LogArtisanWorkshop script, SmithAnvil smithAnvil) {
		super(script);
		this.smithAnvil = smithAnvil;
	}

	@Override
	public String toString() {
		return "Make Sword";
	}

	@Override
	public boolean valid() {
		return !options.finishedSword
				&& options.gotPlan
				&& !ctx.backpack.select().id(TONGS).isEmpty();
	}

	@Override
	public void run() {
		if (!isOpen()) {
			ctx.skillingInterface.close();
			smithAnvil.clickAnvil();
			sleep(333);
			return;
		}

		options.status = "Making sword";

		Sword hitPart = null;

		Sword[] parts = org.powerbot.script.Random.nextBoolean() ? new Sword[]{Sword.Eighth, Sword.Sixteenth} : new Sword[]{Sword.Sixteenth, Sword.Eighth};
		for (Sword part : parts) {
			if (part.getHitsNeeded(ctx) > 0) {
				hitPart = part;
				break;
			}
		}

		if (hitPart == null) {
			final List<Sword> swordList = getSwordPartsByDistance();
			if (swordList.size() > 0) {
				hitPart = swordList.get(org.powerbot.script.Random.nextInt(0, swordList.size()));
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
			//script.log.info("Hit [" + hitPart + "] require=" + hitPart.getHitsNeeded(ctx) + " hittype=" + hitPart.getRequiredHitType(ctx, this));
			hitPart.clickButton(ctx, this);
			sleep(300);
		}
	}

	public boolean closeInterface() {
		if (!isOpen()) {
			return true;
		}

		Component component = ctx.widgets.component(1074, 145);
		if (component.valid() && component.interact("Close")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !isOpen();
				}
			});
		}

		return isOpen();
	}

	public int getCooldown() {
		if (!isOpen()) {
			return Integer.MAX_VALUE;
		}

		return Integer.parseInt(ctx.widgets.component(WIDGET_SWORD_INTERFACE, WIDGET_SWORD_COOLDOWN).text());
	}

	/**
	 * Get sword parts by highest discrepancy first
	 *
	 * @return
	 */
	public List<Sword> getSwordPartsByDistance() {
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
				if (sword.getRequiredHitType(ctx, this) != hitType) {
					break;
				}
				newList.add(sword);
			}

			return newList;
		}

		return list;
	}

	public boolean isOpen() {
		return ctx.widgets.component(WIDGET_SWORD_INTERFACE, WIDGET_SWORD_COOLDOWN).valid();
	}

	static class SwordComparator extends IClientAccessor implements Comparator<Sword> {
		SwordComparator(IClientContext context) {
			super(context);
		}

		@Override
		public int compare(Sword o1, Sword o2) {
			return o2.getHitsNeeded(ctx) - o1.getHitsNeeded(ctx);
		}
	}
}
