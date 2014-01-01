package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith.Track100;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith.Track40;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith.Track60;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith.Track80;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:46
 */
public class SmithTrack extends Branch {
	public static final int[] RAILS = {20506, 20515, 20520};
	public static final int[] BASE_PLATE = {20507, 20516, 20521};
	public static final int[] TRACK_40 = {20511, 20525, 20529};
	public static final int[] SPIKES = {20508, 20517, 20522};
	public static final int[] TRACK_60 = {20512, 20526, 20530};
	public static final int[] JOINT = {20509, 20518, 20523};
	public static final int[] TRACK_80 = {20513, 20527, 20531};
	public static final int[] TIE = {20510, 20519, 20524};
	public static final int[] TRACK_100 = {20514, 20528, 20532};
	private static final int[] PARTIAL_TRACK = {20511, 20525, 20529, 20512, 20526, 20530, 20513, 20527, 20531};
	public static int animationTimelimit = Random.nextInt(2000, 4000);
	public static final int[] PARTS;

	static {
		List<Integer> ids = new ArrayList<Integer>();

		for (int i : RAILS) {
			ids.add(i);
		}
		for (int i : BASE_PLATE) {
			ids.add(i);
		}
		for (int i : TRACK_40) {
			ids.add(i);
		}
		for (int i : SPIKES) {
			ids.add(i);
		}
		for (int i : TRACK_60) {
			ids.add(i);
		}
		for (int i : JOINT) {
			ids.add(i);
		}
		for (int i : TRACK_80) {
			ids.add(i);
		}
		for (int i : TIE) {
			ids.add(i);
		}
		for (int i : TRACK_100) {
			ids.add(i);
		}
		for (int i : PARTIAL_TRACK) {
			ids.add(i);
		}

		PARTS = convertIntegers(ids);
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i);
		}
		return ret;
	}

	@Override
	public String toString() {
		return "SmithTrack";
	}

	private SmithAnvil anvil;

	public SmithTrack(LogicailMethodContext ctx) {
		super(ctx);
		anvil = new SmithAnvil(ctx);

		tasks.add(new Track100(ctx, this));
		tasks.add(new Track80(ctx, this));
		tasks.add(new Track60(ctx, this));
		tasks.add(new Track40(ctx, this));
	}

	public static int getRails() {
		return RAILS[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getBasePlate() {
		return BASE_PLATE[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getTrack40() {
		return TRACK_40[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getSpikes() {
		return SPIKES[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getTrack60() {
		return TRACK_60[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getJoint() {
		return JOINT[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getTrack80() {
		return TRACK_80[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getTie() {
		return TIE[LogArtisanArmourer.ingotType.ordinal()];
	}

	public static int getTrack100() {
		return TRACK_100[LogArtisanArmourer.ingotType.ordinal()];
	}

	public boolean anvilReady() {
		if (!ctx.skillingInterface.getAction().equalsIgnoreCase("Smith")) {
			if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
				return false;
			}

			if (ctx.skillingInterface.isProductionInterfaceOpen()) {
				ctx.skillingInterface.cancelProduction();
				return false;
			}

			anvil.clickAnvil();
		}
		return ctx.skillingInterface.getAction().equalsIgnoreCase("Smith");
	}

	public void smith(int makeid) {
		smith(makeid, 0);
	}

	public void smith(final int makeid, int quanity) {
		//ctx.log.info("Smith: " + makeid + " " + quanity);
		if (quanity > 0 ? ctx.skillingInterface.select(SmithTrack.getCategoryName(), makeid, quanity) : ctx.skillingInterface.select(SmithTrack.getCategoryName(), makeid)) {
			String name = ctx.skillingInterface.getSelectedName();
			final int target = ctx.backpack.select().id(makeid).count() + ctx.skillingInterface.getQuantity();
			if (ctx.skillingInterface.start()) {
				LogArtisanArmourer.isSmithing = true;
				//animationTimelimit = Random.nextInt(2000, 5000);
				LogArtisanArmourer.status = "Smithing " + name;

				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.isPaused()
								|| ctx.isShutdown()
								|| ctx.backpack.select().id(makeid).count() >= target
								|| AnimationMonitor.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING) > 4000;
					}
				}, 600, 100);
				sleep(200, 1000);
				LogArtisanArmourer.isSmithing = false;

				/*if (Random.nextInt(0, 5) == 0) {
					sleep(500, 3000);
					//Util.mouseOffScreen();
				}*/
/*
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.skillingInterface.isOpen() && ctx.players.local().getAnimation() != -1;
					}
				})) {
					for (int id : LogArtisanArmourer.ANIMATION_SMITHING) {
						AnimationMonitor.put(id);
					}
				}*/
			}
		}
	}

	public static String getCategoryName() {
		switch (LogArtisanArmourer.ingotType) {
			case BRONZE:
				return "Bronze Tracks";
			case IRON:
				return "Iron Tracks";
			case STEEL:
				return "Steel Tracks";
			default:
				return "Bronze Tracks";
		}
	}

	@Override
	public boolean branch() {
		Player player = ctx.players.local();

		if (ctx.game.getClientState() != Game.INDEX_MAP_LOADED) {
			return false;
		}

		if (player == null || !LogArtisanArmourer.getAreaSmall().contains(player)) {
			return false;
		}

		if (!ctx.backpack.isFull()) {
			return false;
		}

		if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.getAction().equals("Smith")) {
			return true;
		}

		if (!LogArtisanArmourer.isSmithing) {
			//ctx.log.info("SmithTrack branch not smithing");
			return true;
		}

		if (AnimationMonitor.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING) > animationTimelimit) {
			animationTimelimit = Random.nextInt(2000, 4000);
			if (Random.nextBoolean()) {
				animationTimelimit *= 2;
			}
			return true;
		}

		return false;
	}
}
