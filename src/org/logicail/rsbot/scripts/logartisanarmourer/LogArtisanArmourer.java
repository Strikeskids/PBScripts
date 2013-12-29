package org.logicail.rsbot.scripts.logartisanarmourer;


import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.logartisanarmourer.gui.NewGUI;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.AntiBan;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.DepositOre;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.MakeIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.StayInArea;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.DepositArmour;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect.Ancestors;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect.BrokenPipes;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.GetPlan;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.GetTongs;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.HeatIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.LayTracks;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.TakeIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotType;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.logicail.rsbot.util.ErrorDialog;
import org.logicail.rsbot.util.LinkedProperties;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.SkillData;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Tile;

import javax.swing.*;

@Manifest(
		name = "LogArtisanArmourer",
		description = "Cheap smithing xp at Artisans Workshop",
		version = 2,
		authors = {"Logicail"}
)
public class LogArtisanArmourer extends LogicailScript implements MessageListener {
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
	public static int swordsSmithed;
	public static int brokenSwords;
	public static boolean gotPlan = true;
	public static int perfectSwords;
	public static int completedTracks;

	/* Settings */
	public static IngotType ingotType = IngotType.IRON;
	public static IngotGrade ingotGrade = IngotGrade.ONE;
	public static boolean respectAncestors;
	public static boolean respectRepairPipes;
	public static int ingotsSmithed;
	public static String status = "Setting up...";
	public static boolean isSmithing;
	public static String currentlyMaking = "";
	public static Mode mode = Mode.BURIAL_ARMOUR;
	public static int failedConsecutiveWithdrawals;

	public LogArtisanArmourer() {
		super();
	}

	private SkillData skillData = null;
	private int currentLevel = -1;
	private int startLevel = -1;

	@Override
	public LinkedProperties getPaintInfo() {
		LinkedProperties properties = new LinkedProperties();

		if (ctx.game.isLoggedIn()) {
			if (skillData == null) {
				skillData = new SkillData(ctx);
			}
			currentLevel = ctx.skills.getLevel(Skills.SMITHING);
			if (startLevel == -1) {
				startLevel = currentLevel;
			}
		}

		final long runtime = getRuntime();

		properties.put("Status", status);
		properties.put("Time Running: ", Timer.format(runtime));

		if (skillData != null) {
			properties.put("TTL: ", Timer.format(skillData.timeToLevel(SkillData.Rate.HOUR, Skills.SMITHING)));
			properties.put("Level: ", currentLevel + " (+" + (currentLevel - startLevel) + ")");
			properties.put("XP Gained", String.format("%,d", skillData.experience(Skills.SMITHING)));
			properties.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.SMITHING)));

			final float time = runtime / 3600000f;

			switch (mode) {
				case BURIAL_ARMOUR:
					properties.put("Ingots Smithed", String.format("%,d (%,d/h)", ingotsSmithed, (int) (ingotsSmithed / time)));
					break;
				case CEREMONIAL_SWORDS:
					properties.put("Swords Smithed", String.format("%,d (%,d/h)", swordsSmithed, (int) (swordsSmithed / time)));
					properties.put("Perfect Swords", String.format("Perfect Swords: %,d (%,d/h)", perfectSwords, (int) (perfectSwords / time)));
					properties.put("Broken Swords", String.format("Broken Swords: %,d (%,d/h)", brokenSwords, (int) (brokenSwords / time)));
					break;
				case REPAIR_TRACK:
					properties.put("Completed Tracks", String.format("%,d (%,d/h)", completedTracks, (int) (completedTracks / time)));
					break;
			}
		}

		//properties.add("SkillingQuanitity: " + ctx.skillingInterface.getQuantity());
		//properties.add("TimeAnim: " + AnimationMonitor.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING));

		return properties;
	}

	public static Area getAreaSmall() {
		switch (mode) {
			case BURIAL_ARMOUR:
				return AREAS_ARTISAN_WORKSHOP_BURIAL;
			case CEREMONIAL_SWORDS:
				return AREAS_ARTISAN_WORKSHOP_SWORDS;
			case REPAIR_TRACK:
				return AREAS_ARTISAN_WORKSHOP_TRACKS;
		}

		return AREAS_ARTISAN_WORKSHOP_BURIAL;
	}

	public static int getIngotID() {
		return 20632 + (ingotGrade.ordinal() * 5) + ingotType.ordinal() - 1 + ((ingotGrade.ordinal() >= IngotGrade.FOUR.ordinal()) ? 1 : 0);
	}

	public void createTree() {
		// Check have required level
		try {
			if (ctx.skills.getRealLevel(Skills.SMITHING) < getRequiredLevel()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							new ErrorDialog(null, "Error", "You do not have the required level (" + getRequiredLevel() + ")");
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
				getController().stop();
				return;
			}
		} catch (NullPointerException npe) {
		}

		//tree.add(new SpinTicket());
		//tree.add(new LogoutIdle());
		//tree.add(new EraseChatText());
		//tree.add(new GetSmithingLevel());

		tree.add(new StayInArea(ctx));

		if (respectRepairPipes) {
			tree.add(new BrokenPipes(ctx));
		}

		switch (mode) {
			case BURIAL_ARMOUR:
				if (respectAncestors) {
					//LogHandler.print("Killing ancestors disabled - not updated for eoc");
					//logHandler.print("Add ancestors");
					tree.add(new Ancestors(ctx));
				}

				tree.add(new DepositOre(ctx));
				tree.add(new DepositArmour(ctx));
				tree.add(new MakeIngots(ctx));
				tree.add(new SmithAnvil(ctx));
				break;
			case CEREMONIAL_SWORDS:
				tree.add(new DepositOre(ctx));
				tree.add(new MakeIngots(ctx));
				tree.add(new GetTongs(ctx));
				tree.add(new GetPlan(ctx));
				tree.add(new HeatIngots(ctx));
				tree.add(new MakeSword(ctx));
				break;
			case REPAIR_TRACK:
				tree.add(new TakeIngots(ctx));
				tree.add(new SmithTrack(ctx));
				tree.add(new LayTracks(ctx));
				break;
		}
	}

	@Override
	public void start() {
		super.start();

		// Change this static api crap
		new MakeSword(ctx);

		submit(new AnimationMonitor(ctx));
		submit(new AntiBan(ctx));

		submit(new Task(ctx) {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							new NewGUI(LogArtisanArmourer.this);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			}

			@Override
			public boolean activate() {
				return true;
			}
		});

		/*try {
			final File file = download("http://logicail.co.uk/resources/fonts/OpenSans-Regular.ttf", "OpenSans-Regular.ttf");
			final Font font = FontLoader.load(file);
			Painter.FONT = font.deriveFont(Font.BOLD, 14);
			Painter.FONT_SMALL = font.deriveFont(Font.BOLD, 12);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		//Painter.FONT
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
			String s = e.getMessage();
			switch (e.getId()) {
				case 0:
					if (s.equals("You need plans to make a sword.")) {
						gotPlan = false;
					} else if (s.equals("This sword is too cool to work. Ask Egil or Abel to rate it.") || s.equals("This sword has cooled and you can no longer work it.")) {
						MakeSword.finishedSword = true;
						gotPlan = false;

					} else if (s.equals("You broke the sword! You'll need to get another set of plans from Egil.")) {
						brokenSwords++;
						MakeSword.finishedSword = true;
						gotPlan = false;

					} else if (s.equals("This sword is now perfect and requires no more work.") || s.equals("This sword is perfect. Ask Egil or Abel to rate it.")) {
						MakeSword.finishedSword = true;
						gotPlan = false;

					} else if (s.equals("For producing a perfect sword, you are awarded 120% of the normal experience. Excellent work!")) {
						perfectSwords++;
						swordsSmithed++;
					} else if (s.startsWith("Your sword is awarded")) {
						swordsSmithed++;
						MakeSword.finishedSword = true;
						gotPlan = false;
					}
					break;
				case 109:
					if (s.contains("You make a")) {
						ingotsSmithed++;
					} else if (s.endsWith("track 100%.")) {
						completedTracks++;
					}
					break;
			}
		}
	}

	private int getRequiredLevel() {
		int requiredLevel = 30;
		switch (LogArtisanArmourer.mode) {
			case BURIAL_ARMOUR:
				switch (ingotType) {
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
				switch (ingotType) {
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
				switch (ingotType) {
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
}