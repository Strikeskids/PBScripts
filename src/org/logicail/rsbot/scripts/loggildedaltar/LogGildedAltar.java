package org.logicail.rsbot.scripts.loggildedaltar;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.loggildedaltar.gui.LogGildedAltarGUI;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.*;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.RoomStorage;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HousePortal;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.LeaveHouse;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille.YanilleLodestone;
import org.logicail.rsbot.util.LinkedProperties;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.SkillData;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:49
 */
@Manifest(
		name = "Log Gilded Altar",
		description = "Train prayer at your own or someone else's gilded altar",
		version = 6.012,
		hidden = true,
		authors = {"Logicail"}
)
public class LogGildedAltar extends LogicailScript<LogGildedAltar> implements MessageListener {
	private static final String NEW_VERSION_STRING = "A new version is available (restart script)";
	public LogGildedAltarOptions options = new LogGildedAltarOptions(this);
	public SummoningTask summoningTask;
	public HouseHandler houseHandler = new HouseHandler(this);
	public BankingTask bankingTask;
	public HouseTask houseTask;
	public HousePortal housePortal = new HousePortal(this);
	public RoomStorage roomStorage = new RoomStorage(this);
	public LeaveHouse leaveHouse;

	public YanilleLodestone yanilleLodestone = new YanilleLodestone(this);
	public AltarTask altarTask;
	public AtomicLong nextSummon = new AtomicLong();

	private SkillData skillData = null;
	private int currentLevel = -1;
	private int startLevel = -1;
	public AtomicBoolean familiarFailed = new AtomicBoolean();

	@Override
	public LinkedProperties getPaintInfo() {
		final LinkedProperties properties = new LinkedProperties();
		properties.put("Not everything tested", "Report errors on forum");

		currentLevel = ctx.skills.getLevel(Skills.PRAYER);
		if (currentLevel > 0) {
			if (skillData == null) {
				skillData = new SkillData(ctx);
			}
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
			properties.put("XP Gained", String.format("%,d", experience()));
			properties.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.PRAYER)));
			if (currentLevel < 99) {
				properties.put("XP to level " + (currentLevel + 1), String.format("%,d", org.logicail.rsbot.scripts.framework.util.SkillData.getExperenceToNextLevel(ctx, Skills.PRAYER)));
			}
		}

		final float time = runtime / 3600000f;
		properties.put("Bones Offered", String.format("%,d (%,d/h)", options.bonesOffered.get(), (int) (options.bonesOffered.get() / time)));

		return properties;
	}

	private volatile int experience = 0;

	public int experience() {
		if (skillData != null && currentLevel > 1) {
			try {
				return experience = skillData.experience(Skills.PRAYER);
			} catch (Exception ignored) {
			}
		}
		if (experience > 0) {
			return experience;
		}
		return (int) (options.bonesOffered.get() * options.offering.getXp());
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		String message = messageEvent.getMessage();

		switch (messageEvent.getId()) {
			case 0:
				if (message.equals("They have locked their house to visitors.") || message.equals("They do not seem to be at home.") || message.equals("That player is in building mode at the moment.")) {
					log.info("House not available");
					houseHandler.currentHouseFailed();
					if (!options.detectHouses.get() && houseHandler.getOpenHouse() == null) {
						ctx.stop("Can't enter friends house anymore");
					}
				} else if (message.equals("Your beast of burden has no more items.")) {
					options.usedBOB.set(true);
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
					if (message.startsWith("Your familiar is too big to fit here.")) {
						familiarFailed.set(true);
					}/* else if (options.useAura && message.startsWith("Currently recharging.")) {
						try {
							int start = message.indexOf("<col=ff0000>") + 12;
							String timeString = message.substring(start, message.indexOf(" ", start));
							String[] hms = timeString.split(":");
							int seconds = Integer.parseInt(hms[0]) * 3600 + Integer.parseInt(hms[1]) * 60 + Integer.parseInt(hms[2]);
							//ActivateAura.setTimeNextAura(System.currentTimeMillis() + Random.nextInt(seconds * 1000, seconds * 1020)); // TODO
						} catch (Exception ignored) {
						}
					}*/ else if (message.equals("The spirit in this pouch is too big to summon here. You will need to move to a larger area.")) {
						familiarFailed.set(true);
					}
				}
				break;
			case 2:
				if (options.detectHouses.get()) {
					ctx.log.info("Chat[2]: " + message);
					houseHandler.parseHouses(message);
				}
				break;
			case 4:
				if (message.equals("That player is offline, or has privacy mode enabled.")) {
					options.status = "Can't enter friends house anymore";
					log.info("Can't enter friends house anymore");

					getController().getExecutor().offer(new Task<LogGildedAltar>(this) {
						@Override
						public void run() {
							houseHandler.currentHouseFailed();
							if (!options.detectHouses.get() && houseHandler.getOpenHouse() == null) {
								ctx.stop("Can't enter friends house anymore");
							}
						}
					});
				}
				break;
			case 109:
				if (message.equals("Note: anything your familiar is carrying when it disappears will be placed on the floor.")) {
					options.usedBOB.set(false);
				} else if (message.contains("your offering.")) {
					options.bonesOffered.incrementAndGet();
				}
		}
	}

	@Override
	public void repaint(Graphics g) {
		super.repaint(g);

		if (options.newVersionAvailable.get()) {
			final Point point = ctx.mouse.getLocation();
			int x = point.x + 10;
			int y = point.y + 6;
			g.setColor(Color.BLACK);
			g.drawString(NEW_VERSION_STRING, x + 1, y + 1);
			g.setColor(Color.RED);
			g.drawString(NEW_VERSION_STRING, x, y);
		}

		/*final Tile location = ctx.players.local().getLocation();
		LogicailArea area = new LogicailArea(location.derive(-8, -8), location.derive(8, 8));
		final Tile tile = area.findSpace(ctx, 3, 3);
		tile.getMatrix(ctx).draw(g);*/
	}

	@Override
	public void start() {
		getController().getExecutor().offer(new Task<LogGildedAltar>(this) {
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
