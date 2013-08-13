package org.logicail.scripts.logartisanarmourer.tasks.track;

import org.logicail.api.methods.providers.Condition;
import org.logicail.framework.script.state.BranchOnce;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.tasks.Anvil;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;
import org.logicail.scripts.logartisanarmourer.tasks.track.smith.Track100;
import org.logicail.scripts.logartisanarmourer.tasks.track.smith.Track40;
import org.logicail.scripts.logartisanarmourer.tasks.track.smith.Track60;
import org.logicail.scripts.logartisanarmourer.tasks.track.smith.Track80;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 12:06
 */
public class SmithTrack extends BranchOnce {
	//private static final int[] PARTIAL_TRACK = {20511, 20525, 20529, 20512, 20526, 20530, 20513, 20527, 20531};
	public static int animationTimelimit = Random.nextInt(4000, 7000);
	private LogArtisanArmourerOptions options;
	private Anvil anvil;

	public SmithTrack(Tracks tracks) {
		super(tracks.ctx);

		anvil = new Anvil(ctx);

		options = ((LogArtisanArmourer) ctx.script).options;

		nodes.add(new Track100(tracks));
		nodes.add(new Track80(tracks));
		nodes.add(new Track60(tracks));
		nodes.add(new Track40(tracks));
	}

	public boolean anvilReady() {
		if (!ctx.skillingInterface.getAction().equals("Smith")) {
			if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
				return false;
			}

			if (ctx.skillingInterface.isProductionInterfaceOpen()) {
				ctx.skillingInterface.cancelProduction();
				return false;
			}

			anvil.click();
			return false;
		}
		return true;
	}

	public void smith(int makeid) {
		smith(makeid, 0);
	}

	public void smith(int makeid, int quanity) {
		if (quanity > 0 ? ctx.skillingInterface.select(getCategoryName(), makeid, quanity) : ctx.skillingInterface.select(getCategoryName(), makeid)) {
			//String name = ctx.skillingInterface.getSelectedName();
			if (ctx.skillingInterface.start()) {
				options.isSmithing = true;
				//animationTimelimit = Random.nextInt(2000, 5000);
				//options.status = "Smithing " + name;

				if (Random.nextInt(0, 5) == 0) {
					sleep(500, 3000);
					//Util.mouseOffScreen();
				}

				if (ctx
						.waiting.wait(4500, new Condition() {
							@Override
							public boolean validate() {
								return !ctx.skillingInterface.isOpen() && ctx.players.local().getAnimation() != -1;
							}
						})) {
					for (int id : LogArtisanArmourer.ANIMATION_SMITHING) {
						ctx.animationHistory.put(id);
					}
				}
			}
		}
	}

	public String getCategoryName() {
		switch (options.ingotType) {
			case BRONZE:
				return "Bronze Tracks";
			case IRON:
				return "Iron Tracks";
			case STEEL:
				return "Steel Tracks";
		}
		return "Steel Tracks";
	}

	@Override
	public boolean branch() {
		if (ctx.game.getClientState() != Game.INDEX_MAP_LOADED) {
			return false;
		}

		if (!ctx.backpack.isFull()) {
			return false;
		}

		if (ctx.skillingInterface.getAction().equals("Smith")) {
			return true;
		}

		if (ctx.animationHistory.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING) > animationTimelimit) {
			animationTimelimit = Random.nextInt(4000, 7000);
			return true;
		}

		return false;
	}
}
