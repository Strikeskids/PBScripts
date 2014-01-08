package org.logicail.rsbot.scripts.loggildedaltar;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.context.providers.Lodestones;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.loggildedaltar.gui.LogGildedAltarGUI;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.*;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.RoomStorage;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HousePortal;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.LeaveHouse;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille.YanilleLodestone;
import org.logicail.rsbot.util.LinkedProperties;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.SkillData;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:49
 */
@Manifest(
		name = "LogGildedAltar",
		description = "Train prayer at your own or someone else's gilded altar",
		version = 6,
		hidden = true,
		authors = {"Logicail"}
)
public class LogGildedAltar extends LogicailScript<LogGildedAltar> implements MessageListener {
	public LogGildedAltarOptions options = new LogGildedAltarOptions(this);
	public SummoningTask summoningTask;
	public HouseHandler houseHandler = new HouseHandler(this);
	public BankingTask bankingTask;
	public HouseTask houseTask;
	public HousePortal housePortal = new HousePortal(this);
	public RoomStorage roomStorage;
	public LeaveHouse leaveHouse;

	public YanilleLodestone yanilleLodestone = new YanilleLodestone(this);
	public AltarTask altarTask;

	private SkillData skillData = null;
	private int currentLevel = -1;
	private int startLevel = -1;

	@Override
	public LinkedProperties getPaintInfo() {
		final LinkedProperties properties = new LinkedProperties();

		if (ctx.game.isLoggedIn()) {
			if (skillData == null) {
				skillData = new SkillData(ctx);
			}
			currentLevel = ctx.skills.getLevel(Skills.PRAYER);
			if (startLevel == -1) {
				startLevel = currentLevel;
			}
		}

		final long runtime = getRuntime();

		properties.put("Status", options.status);
		properties.put("Time Running", org.powerbot.script.util.Timer.format(runtime));

		if (skillData != null) {
			properties.put("TTL", org.powerbot.script.util.Timer.format(skillData.timeToLevel(SkillData.Rate.HOUR, Skills.PRAYER)));
			properties.put("Level", String.format("%d (+%d)", currentLevel, currentLevel - startLevel));
			properties.put("XP Gained", String.format("%,d", skillData.experience(Skills.PRAYER)));
			properties.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.PRAYER)));
		}

		final float time = runtime / 3600000f;
		properties.put("Bones Offered", String.format("%,d (%,d/h)", options.bonesOffered, (int) (options.bonesOffered / time)));

//		if (currentLevel < 99) {
//			properties.put("XP to level " + currentLevel + 1, String.format("%,d", .getExperienceToLevel(Skills.PRAYER, level + 1)), 575, 30, new Color(0, 0, 0, 64), Color.WHITE);
//		}

//		properties.put("ELDER_TREE_SETTING", ctx.settings.get(3881));
//		for (int i = 0; i < 10; i++) {
//			properties.put("ELDER_TREE_" + i, ctx.settings.get(3881, i, 0x1) == 0x1);
//		}
		final Lodestones.Lodestone previousDestination = ctx.lodestones.getPreviousDestination();
		properties.put("Previous Lodestone", previousDestination == null ? "null" : previousDestination.name());

		for (Lodestones.Lodestone lodestone : Lodestones.Lodestone.values()) {
			properties.put(lodestone.name(), lodestone.isUnlocked(ctx));
		}

		return properties;
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		String message = messageEvent.getMessage();

		switch (messageEvent.getId()) {
			case 0:
				if (message.equals("They have locked their house to visitors.") || message.equals("They do not seem to be at home.") || message.equals("That player is in building mode at the moment.")) {
					log.info("House not available");
					houseHandler.currentHouseFailed();
					if (!options.detectHouses && houseHandler.getOpenHouse() == null) {
						ctx.stop("Can't enter friends house anymore");
					}
				} else if (message.equals("Your beast of burden has no more items.")) {
					options.usedBOB = true;
				} else if (message.equals("You cannot turn off an aura. It must deplete in its own time.")) {
					/*if (options.useAura) {
						submit(new Task<LogGildedAltar>(this) {
							@Override
							public void run() {
								//ActivateAura.activatedAura(); // TODO
							}
						});
					}*/
				} else {
					if (message.startsWith("Your familiar is too large to fit into the area you are standing in")) {
						final Player local = ctx.players.local();
						if (local == null) {
							break;
						}
						final Tile currentLocation = local.getLocation();

						final List<Tile> tiles = new ArrayList<Tile>();
						for (Tile tile : new LogicailArea(currentLocation.derive(-5, -5), currentLocation.derive(5, 5)).getTileArray()) {
							double distance = tile.distanceTo(currentLocation);
							if (distance > 2 && distance < 6 && tile.getMatrix(ctx).isReachable()) {
								tiles.add(tile);
							}
						}

						Collections.shuffle(tiles);

						if (!tiles.isEmpty()) {
							log.info("Walk so familiar can be summoned");
							submit(new Task<LogGildedAltar>(this) {
								@Override
								public void run() {
									for (int i = 0; i < 5; ++i) {
										final Tile destination = tiles.get(i);
										if (ctx.movement.findPath(destination).traverse()) {
											sleep(400, 1200);
											if (Condition.wait(new Callable<Boolean>() {
												@Override
												public Boolean call() throws Exception {
													final Player player = ctx.players.local();
													return player == null || !currentLocation.equals(player.getLocation());
												}
											})) {
												break;
											}
										}
									}
								}
							});
						}
					} else if (options.useAura && message.startsWith("Currently recharging.")) {
						try {
							int start = message.indexOf("<col=ff0000>") + 12;
							String timeString = message.substring(start, message.indexOf(" ", start));
							String[] hms = timeString.split(":");
							int seconds = Integer.parseInt(hms[0]) * 3600 + Integer.parseInt(hms[1]) * 60 + Integer.parseInt(hms[2]);
							//ActivateAura.setTimeNextAura(System.currentTimeMillis() + Random.nextInt(seconds * 1000, seconds * 1020)); // TODO
						} catch (Exception ignored) {
						}
					}
				}
				break;
			case 2:
				if (options.detectHouses) {
					houseHandler.parseHouses(message);
				}
				break;
			case 4:
				if (message.equals("That player is offline, or has privacy mode enabled.")) {
					options.status = "Can't enter friends house anymore";
					log.info("Can't enter friends house anymore");

					submit(new Task<LogGildedAltar>(this) {
						@Override
						public void run() {
							houseHandler.currentHouseFailed();
							if (!options.detectHouses && houseHandler.getOpenHouse() == null) {
								ctx.stop("Can't enter friends house anymore");
							}
						}
					});
				}
				break;
			case 109:
				if (message.equals("Note: anything your familiar is carrying when it disappears will be placed on the floor.")) {
					options.usedBOB = false;
				} else if (message.contains("your offering.")) {
					++options.bonesOffered;
				}
		}
	}

	@Override
	public void start() {
		submit(new Task<LogGildedAltar>(this) {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							gui = new LogGildedAltarGUI(LogGildedAltar.this);
						} catch (Exception exception) {
							exception.printStackTrace();
							getController().stop();
						}
					}
				});
			}
		});
	}
}
