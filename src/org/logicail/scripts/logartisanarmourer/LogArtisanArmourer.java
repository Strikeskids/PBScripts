package org.logicail.scripts.logartisanarmourer;

import org.logicail.framework.script.ActiveScript;
import org.logicail.framework.script.state.Node;
import org.logicail.framework.script.state.Tree;
import org.logicail.scripts.logartisanarmourer.paint.Paint;
import org.logicail.scripts.logartisanarmourer.tasks.AntiBan;
import org.logicail.scripts.logartisanarmourer.tasks.StayInArea;
import org.logicail.scripts.logartisanarmourer.tasks.modes.BurialArmour;
import org.logicail.scripts.logartisanarmourer.tasks.modes.CeremonialSwords;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;
import org.logicail.scripts.tasks.IdleLogout;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Skills;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 19/06/13
 * Time: 17:25
 */
@Manifest(
		name = "LogArtisanArmourer",
		description = "Cheap smithing at Artisans Workshop",
		version = 2.02,
		authors = {"Logicail"},
		hidden = true,
		website = "http://www.powerbot.org/community/topic/704413-logartisan-artisan-armourer-cheap-smither/")
public class LogArtisanArmourer extends ActiveScript implements MessageListener {
	public static final int[] ANIMATION_SMITHING = {898, 11062, 15121};
	public LogArtisanArmourerOptions options = new LogArtisanArmourerOptions();
	public Paint paint;

	@Override
	public void start() {
		ctx.submit(new AntiBan(ctx));
		ctx.submit(super.paint = paint = new Paint(ctx));
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new LogArtisanArmourerGUI(ctx);
			}
		});
	}

	/**
	 * Create the job tree
	 */
	public void create() {
		ArrayList<Node> nodes = new ArrayList<>();

		nodes.add(new IdleLogout(ctx, 15 * 60, 20 * 60));
		nodes.add(new StayInArea(ctx));

		switch (options.mode) {
			case BURIAL_ARMOUR:
				nodes.add(new BurialArmour(ctx));
				break;
			case CEREMONIAL_SWORDS:
				nodes.add(new CeremonialSwords(ctx));
				break;
			case REPAIR_TRACK:
				nodes.add(new Tracks(ctx));
				break;
		}

		tree = new Tree(ctx, nodes);

		log.info(options.toString());
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		if (tree != null) {
			if (ctx.game.isLoggedIn()) {
				int before = ctx.skills.getExperience(Skills.SMITHING);
				final String message = messageEvent.getMessage();
				switch (messageEvent.getId()) {
					case 0:
						switch (message) {
							case "You need plans to make a sword.":
								options.gotPlan = false;
								break;
							case "This sword is too cool to work. Ask Egil or Abel to rate it.":
							case "This sword has cooled and you can no longer work it.":
								options.finishedSword = true;
								options.gotPlan = false;
								break;
							case "You broke the sword! You'll need to get another set of plans from Egil.":
								paint.brokenSwords++;
								options.finishedSword = true;
								options.gotPlan = false;
								break;
							case "This sword is now perfect and requires no more work.":
							case "This sword is perfect. Ask Egil or Abel to rate it.":
								options.finishedSword = true;
								options.gotPlan = false;
								break;
							case "For producing a perfect sword, you are awarded 120% of the normal experience. Excellent work!":
								paint.perfectSwords++;
								paint.swordsSmithed++;
								break;
							default:
								if (message.startsWith("Your sword is awarded")) {
									paint.swordsSmithed++;
									options.finishedSword = true;
									options.gotPlan = false;
								}
								break;
						}
						break;
					case 109:
						if (message.contains("You make a")) {
							paint.ingots++;
						}
						break;
				}

				int now = ctx.skills.getExperience(Skills.SMITHING);
				if (now > before) {
					paint.xpGained += now - before;
				}
			}
		}
	}
}
