package org.logicail.rsbot.scripts.loggildedaltar;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.loggildedaltar.gui.LogGildedAltarGUI;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.*;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LocationAttribute;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
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
import org.powerbot.script.wrappers.Tile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
		version = 6.044,
		hidden = true,
		authors = {"Logicail"}
)
public class LogGildedAltar extends LogicailScript<LogGildedAltar> implements MessageListener {
	private static final String NEW_VERSION_STRING = "A new version is available (restart script)";
	public final LogGildedAltarOptions options = new LogGildedAltarOptions(this);
	public final HouseHandler houseHandler = new HouseHandler(this);
	public final HousePortal housePortal = new HousePortal(this);
	public final RoomStorage roomStorage = new RoomStorage(this);
	public final YanilleLodestone yanilleLodestone = new YanilleLodestone(this);
	public final AtomicLong nextSummon = new AtomicLong();
	public final AtomicBoolean familiarFailed = new AtomicBoolean();
	private final AtomicInteger currentLevel = new AtomicInteger(-1);
	private final AtomicInteger startLevel = new AtomicInteger(-1);
	private final AtomicInteger experience = new AtomicInteger();
	public SummoningTask summoningTask = null;
	public BankingTask bankingTask = null;
	public HouseTask houseTask = null;
	public LeaveHouse leaveHouse = null;
	public AltarTask altarTask = null;
	private SkillData skillData = null;

	@Override
	public LinkedProperties getPaintInfo() {
		final LinkedProperties properties = new LinkedProperties();
		properties.put("Not everything tested", "Report errors on forum");

		if (ctx.game.isLoggedIn()) {
			final int current = ctx.skills.getRealLevel(Skills.PRAYER);
			currentLevel.set(current);
			if (current > 0) {
				if (skillData == null) {
					startLevel.set(current);
					skillData = new SkillData(ctx);
				}
			}
		}

		final int current = currentLevel.get();

		final long runtime = getRuntime();

		properties.put("Status", options.status);
		properties.put("Time Running", org.powerbot.script.util.Timer.format(runtime));

		if (skillData != null) {
			properties.put("TTL", org.powerbot.script.util.Timer.format(skillData.timeToLevel(SkillData.Rate.HOUR, Skills.PRAYER)));
			properties.put("Level", String.format("%d (+%d)", current, current - startLevel.get()));
			properties.put("XP Gained", String.format("%,d", experience()));
			properties.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.PRAYER)));
			if (current < 99) {
				properties.put("XP to level " + (current + 1), String.format("%,d", org.logicail.rsbot.scripts.framework.util.SkillData.getExperenceToNextLevel(ctx, Skills.PRAYER)));
			}
		}

		final float time = runtime / 3600000f;
		properties.put("Bones Offered", String.format("%,d (%,d/h)", options.bonesOffered.get(), (int) (options.bonesOffered.get() / time)));

//		for (ElderTree tree : ElderTree.values()) {
//			properties.put(tree.name(), tree.hasBranches(ctx) + " " + tree.getTime(ctx));
//		}

//		properties.put(1321, ctx.settings.get(1321, 7, 0xf));

		//for (CrystalTree tree : CrystalTree.values()) {
		//	properties.put(tree.name(), tree.hasBranches(ctx)/* + " " + tree.getTime(ctx)*/);
		//}

		return properties;
	}

	public int experience() {
		if (skillData != null && currentLevel.get() > 1) {
			try {
				experience.set(skillData.experience(Skills.PRAYER));
			} catch (Exception ignored) {
			}
		}
		final int get = experience.get();
		if (get > 0) {
			return get;
		}
		return (int) (options.bonesOffered.get() * options.offering.getXp());
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		String message = messageEvent.getMessage();

		switch (messageEvent.getId()) {
			case 0:
				if (message.equals("They have locked their house to visitors.") || message.equals("They do not seem to be at home.") || message.equals("That player is in building mode at the moment.")) {
					housePortal.enteringHouse.set(false);
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
				} else if (options.useBOB.get()) {
					if (message.startsWith("Your familiar is too big to fit here.")
							|| message.equals("The spirit in this pouch is too big to summon here. You will need to move to a larger area.")
							|| message.contains(" too far away ")) {
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
					}*/
				}
				break;
			case 2:
				if (options.detectHouses.get()) {
					//log.info("Chat[2]: " + message);
					houseHandler.parseHouses(message);
				}
				break;
			case 4:
				if (message.equals("That player is offline, or has privacy mode enabled.")) {
					housePortal.enteringHouse.set(false);
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
					options.timesBob.incrementAndGet();
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

//		if (altarTask != null) {
//			final GameObject altar = altarTask.getAltar();
//			if (altar.isValid()) {
//				altar.getLocation().getMatrix(ctx).draw(g);
//			}
//		}

//		final Room room = roomStorage.getRoom(ctx.players.local());
//		if (room != null) {
//			for (Room neighbour : roomStorage.getPossibleNeighbours(room)) {
//				for (Tile tile : neighbour.getArea().getTileArray()) {
//					tile.getMatrix(ctx).draw(g);
//				}
//			}
//		}

//		for (Tile tile : LocationAttribute.EDGEVILLE.getObeliskArea().getTileArray()) {
//			tile.getMatrix(ctx).draw(g);
//		}

		if (summoningTask != null) {
			for (Node<LogGildedAltar> node : summoningTask.getNodes()) {
				if (node instanceof NodePath) {
					final LocationAttribute location = ((NodePath) node).getPath().getLocation();
					for (Tile tile : location.getObeliskArea().getTileArray()) {
						tile.getMatrix(ctx).draw(g);
					}
				}
			}
		}
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
							PrintWriter printWriter = null;
							try {
								printWriter = new PrintWriter(new File(getStorageDirectory(), "exception.log"));
								exception.printStackTrace(printWriter);
								exception.printStackTrace();
								getController().stop();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} finally {
								if (printWriter != null) {
									printWriter.flush();
									printWriter.close();
								}
							}
						}
					}
				});
			}
		});
	}
}
