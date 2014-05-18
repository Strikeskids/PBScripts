package org.logicail.rsbot.scripts.logartisanarmourer;


import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.framework.tasks.impl.AntiBan;
import org.logicail.rsbot.scripts.framework.util.SkillData;
import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.scripts.logartisanarmourer.gui.ArtisanGUI;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.DepositOre;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.MakeIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.StayInArea;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.DepositArmour;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect.Ancestors;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect.BrokenPipes;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect.RespectTask;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.GetPlan;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.GetTongs;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.HeatIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.LayTracks;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.TakeIngots;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.*;
import org.powerbot.script.rt6.Skills;

import javax.swing.*;
import java.util.LinkedHashMap;

@Script.Manifest(
		name = "Log Artisan Workshop",
		description = "Cheap smithing xp at Artisans Workshop",
		properties = "topic=1134701;client=6;hidden=true"
)
public class LogArtisanWorkshop extends LogicailScript<LogArtisanWorkshop> implements MessageListener {
	public static final int[] INGOT_IDS = {20632, 20633, 20634, 20635, 20636,
			20637, 20638, 20639, 20640, 20641, 20642, 20643, 20644, 20645,
			20646, 20647, 20648, 20649, 20650, 20651, 20652};
	public static final int[] ARMOUR_ID_LIST = {20572, 20573, 20574, 20575,
			20576, 20577, 20578, 20579, 20580, 20581, 20582, 20583, 20584,
			20585, 20586, 20587, 20588, 20589, 20590, 20591, 20592, 20593,
			20594, 20595, 20596, 20597, 20598, 20599, 20600, 20601, 20602,
			20603, 20604, 20605, 20606, 20607, 20608, 20609, 20610, 20611,
			20612, 20613, 20614, 20615, 20616, 20617, 20618, 20619, 20620,
			20621, 20622, 20623, 20624, 20625, 20626, 20627, 20628, 20629,
			20630, 20631};
	public static final Area AREAS_ARTISAN_WORKSHOP_BURIAL = new Area(new Tile(3050, 3345, 0), new Tile(3062, 3333, 0));
	public static final Area AREAS_ARTISAN_WORKSHOP_SWORDS = new Area(new Tile(3034, 3346, 0), new Tile(3049, 3332, 0));
	public static final Area AREAS_ARTISAN_WORKSHOP_TRACKS = new Area(new Tile(3054, 9719, 0), new Tile(3080, 9704, 0));
	public static final Area AREAS_ARTISAN_WORKSHOP_LARGE = new Area(new Tile(3040, 3344, 0), new Tile(3061, 3333, 0));
	public static final int ID_MINE_CART = 24824;
	public static final int ID_SMELTER = 29395;
	public static final int ID_SMELTER_SWORDS = 29394;
	public static final int[] ANIMATION_SMITHING = {898, 11062, 15121};
	public final LogArtisanWorkshopOptions options = new LogArtisanWorkshopOptions();
	private SkillData skillData = null;
	private int currentLevel = -1;
	private int startLevel = -1;
	private DepositOre depositOre = null;

	private int getRequiredLevel() {
		int requiredLevel = 30;
		switch (options.mode) {
			case BURIAL_ARMOUR:
				switch (options.ingotType) {
					case IRON:
						requiredLevel = 30;
						break;
					case STEEL:
						requiredLevel = 45;
						break;
					case MITHRIL:
						requiredLevel = 60;
						break;
					case ADAMANT:
						requiredLevel = 70;
						break;
					case RUNE:
						requiredLevel = 90;
						break;
				}
				break;
			case CEREMONIAL_SWORDS:
				switch (options.ingotType) {
					case IRON:
						requiredLevel = 70;
						break;
					case STEEL:
						requiredLevel = 75;
						break;
					case MITHRIL:
						requiredLevel = 80;
						break;
					case ADAMANT:
						requiredLevel = 85;
						break;
					case RUNE:
						requiredLevel = 90;
						break;
				}
			case REPAIR_TRACK:
				switch (options.ingotType) {
					case BRONZE:
						requiredLevel = 12;
						break;
					case IRON:
						requiredLevel = 35;
						break;
					case STEEL:
						requiredLevel = 60;
						break;
				}
		}
		return requiredLevel;
	}

	public void createTree() {
		// Check have required level
		try {
			if (ctx.skills.realLevel(Skills.SMITHING) < getRequiredLevel()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							new ErrorDialog("Error", "You do not have the required level (" + getRequiredLevel() + ")");
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
				ctx.controller.stop();
				return;
			}
		} catch (NullPointerException ignored) {
		}

		ctx.submit(new AnimationMonitor<LogArtisanWorkshop>(this));
		ctx.submit(new AntiBan<LogArtisanWorkshop>(this));

		//tree.add(new LogoutIdle());

		tree.add(new StayInArea(this));

		if (options.respectRepairPipes) {
			tree.add(new BrokenPipes(this));
		}

		final SmithAnvil smithAnvil = new SmithAnvil(this);
		final MakeSword makeSword = new MakeSword(this, smithAnvil);
		smithAnvil.setMakeSword(makeSword);
		switch (options.mode) {
			case BURIAL_ARMOUR:
				if (options.respectAncestors) {
					//LogHandler.print("Killing ancestors disabled - not updated for eoc");
					//logHandler.print("Add ancestors");
					tree.add(new Ancestors(this));
				}

				tree.add(depositOre = new DepositOre(this));
				tree.add(new DepositArmour(this));
				tree.add(new MakeIngots(this, smithAnvil));
				tree.add(smithAnvil);
				break;
			case CEREMONIAL_SWORDS:
				tree.add(depositOre = new DepositOre(this));
				tree.add(new GetTongs(this));
				tree.add(new MakeIngots(this, smithAnvil));
				tree.add(new GetPlan(this, makeSword));
				tree.add(new HeatIngots(this, makeSword));
				tree.add(makeSword);
				break;
			case REPAIR_TRACK:
				tree.add(new TakeIngots(this));
				tree.add(new SmithTrack(this, makeSword));
				tree.add(new LayTracks(this));
				break;
		}
	}

	@Override
	public LinkedHashMap<Object, Object> getPaintInfo() {
		LinkedHashMap<Object, Object> properties = new LinkedHashMap<Object, Object>();

		if (ctx.game.loggedIn()) {
			if (skillData == null) {
				skillData = new SkillData(ctx);
			}
			currentLevel = ctx.skills.realLevel(Skills.SMITHING);
			if (startLevel == -1) {
				startLevel = currentLevel;
			}
		}

		final long runtime = getRuntime();

		properties.put("Status", options.status);
		properties.put("Time Running", Timer.format(runtime));

		if (skillData != null) {
			properties.put("TTL", Timer.format(skillData.timeToLevel(SkillData.Rate.HOUR, Skills.SMITHING)));
			properties.put("Level", String.format("%d (+%d)", currentLevel, currentLevel - startLevel));
			properties.put("XP Gained", String.format("%,d", skillData.experience(Skills.SMITHING)));
			properties.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.SMITHING)));
		}

		final float time = runtime / 3600000f;

		switch (options.mode) {
			case BURIAL_ARMOUR:
				properties.put("Currently making", options.currentlyMaking);
				properties.put("Ingots Smithed", String.format("%,d (%,d/h)", options.ingotsSmithed, (int) (options.ingotsSmithed / time)));
				break;
			case CEREMONIAL_SWORDS:
				properties.put("Swords Smithed", String.format("%,d (%,d/h)", options.swordsSmithed, (int) (options.swordsSmithed / time)));
				properties.put("Perfect Swords", String.format("%,d (%,d/h)", options.perfectSwords, (int) (options.perfectSwords / time)));
				properties.put("Broken Swords", String.format("%,d (%,d/h)", options.brokenSwords, (int) (options.brokenSwords / time)));
				break;
			case REPAIR_TRACK:
				properties.put("Completed Tracks", String.format("%,d (%,d/h)", options.completedTracks, (int) (options.completedTracks / time)));
				break;
		}

		properties.put("Respect", RespectTask.getRespect(ctx));

//		if (depositOre != null) {
//			properties.put("Iron", depositOre.remainingIron());
//			properties.put("Coal", depositOre.remainingCoal());
//			properties.put("Mithril", depositOre.remainingMithril());
//			properties.put("Adamant", depositOre.remainingAdamant());
//			properties.put("Runite", depositOre.remainingRune());
//		}

		//properties.add("SkillingQuanitity: " + ctx.skillingInterface.getQuantity());
		//properties.add("TimeAnim: " + AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING));
		//properties.put("SKCat", ctx.skillingInterface.getCategory());
		//properties.put("Iron3", IngotType.IRON.getID(IngotGrade.THREE));
		//properties.put("Iron4", IngotType.IRON.getID(IngotGrade.FOUR));
		//properties.put("Steel3", IngotType.STEEL.getID(IngotGrade.THREE));

		/*final List<String> categorys = ctx.skillingInterface.getCategorys();
		if (categorys.size() > 0) {
			StringBuilder stringBuilder = new StringBuilder("\"");
			for (String s : categorys) {
				stringBuilder.append(s).append("\", ");
			}
			final String s = stringBuilder.toString();
			if (!s.isEmpty()) {
				properties.put("Categorys", s.substring(0, s.length() - 2));
			}
		}*/

		return properties;
	}

	@Override
	public void messaged(MessageEvent e) {
		// TODO: Need message for when smithing level not high enough

		if (tree.any()) {
		    /*if (e.getId() == 0) {
		        if (e.getMessage().contains("You don't have enough")) {
                    ++failedConsecutiveWithdrawals;
                    if (failedConsecutiveWithdrawals >= 3) {
                        log.log(Level.SEVERE, "Ran out of ore... stopping!");
                        shutdown();
                    }
                } else if (e.getMessage().contains("You collect the"))
                    failedConsecutiveWithdrawals = 0;
            } else */
			String s = e.text();
			switch (e.type()) {
				case 0:
					if (s.equals("You need plans to make a sword.")) {
						options.gotPlan.set(false);
					} else if (s.equals("This sword is too cool to work. Ask Egil or Abel to rate it.") || s.equals("This sword has cooled and you can no longer work it.")) {
						options.finishedSword = true;
						options.gotPlan.set(false);
					} else if (s.equals("You broke the sword! You'll need to get another set of plans from Egil.")) {
						options.brokenSwords++;
						options.finishedSword = true;
						options.gotPlan.set(false);
					} else if (s.equals("This sword is now perfect and requires no more work.") || s.equals("This sword is perfect. Ask Egil or Abel to rate it.")) {
						options.finishedSword = true;
						options.gotPlan.set(false);
					} else if (s.equals("For producing a perfect sword, you are awarded 120% of the normal experience. Excellent work!")) {
						options.perfectSwords++;
						options.swordsSmithed++;
					} else if (s.startsWith("Your sword is awarded")) {
						options.swordsSmithed++;
						options.finishedSword = true;
						options.gotPlan.set(false);
					}
					break;
				case 109:
					if (s.contains("You make a")) {
						options.ingotsSmithed++;
					} else if (s.endsWith("track 100%.")) {
						options.completedTracks++;
					}
					break;
			}
		}
	}

//	@Override
//	public void repaint(Graphics g) {
//		super.repaint(g);
//
//
//		// Changed to using settings
//		// Debug incase repairing pipes doeesn't work (Haven't been able to test yet)
////		if (options.mode != Mode.REPAIR_TRACK && options.respectRepairPipes) {
////			for (GameObject pipe : ctx.objects.select().id(BrokenPipes.BROKEN_PIPE)) {
////				if (pipe.isOnScreen()) {
////					final AbstractModel abstractModel = pipe.getInternal().getModel();
////					if (abstractModel == null) {
////						continue;
////					}
////					final Point point = pipe.getCenterPoint();
////					final String s = String.format("Pipe: %d", BrokenPipes.getNumFaces(abstractModel));
////					g.setColor(Color.BLACK);
////					g.drawString(s, point.x + 1, point.y + 1);
////					g.setColor(Color.WHITE);
////					g.drawString(s, point.x, point.y);
////				}
////			}
////		}
//	}

	@Override
	public void start() {
		ctx.controller.offer(new Task<LogArtisanWorkshop>(this) {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							gui = new ArtisanGUI(LogArtisanWorkshop.this);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			}
		});

		/*try {
			final File file = download("http://logicail.co.uk/resources/fonts/OpenSans-Regular.ttf", "OpenSans-Regular.ttf");
			final Font font = FontLoader.load(file);
			Painter.FONT_TITLE = font.deriveFont(Font.BOLD, 14);
			Painter.FONT_SMALL = font.deriveFont(Font.BOLD, 12);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		//Painter.FONT_TITLE
	}
}
