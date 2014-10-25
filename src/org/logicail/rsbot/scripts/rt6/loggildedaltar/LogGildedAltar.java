package org.logicail.rsbot.scripts.rt6.loggildedaltar;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.util.SkillData;
import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.gui.LogGildedAltarGUI;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.*;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar.RoomStorage;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house.HousePortal;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house.LeaveHouse;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.Skills;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FYI this script is a mess it was my first script written for RSBot 4 but I cba rewriting it since it is so big
 * <p/>
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:49
 */
@Script.Manifest(
		name = "Log Gilded Altar",
		description = "Train prayer at your own or someone else's gilded altar",
		properties = "topic=1141536;client=6;version=7.01"
)
public class LogGildedAltar extends LogicailScript<LogGildedAltar> implements MessageListener {
	private static final String NEW_VERSION_STRING = "A new version is available (restart script)";
	public final LogGildedAltarOptions options = new LogGildedAltarOptions(this);
	public final HouseHandler houseHandler = new HouseHandler(this);
	public final HousePortal housePortal = new HousePortal(this);
	public final RoomStorage roomStorage = new RoomStorage(this);
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
	public LinkedHashMap<Object, Object> getPaintInfo() {
		final LinkedHashMap<Object, Object> properties = new LinkedHashMap<Object, Object>();

		if (ctx.game.loggedIn()) {
			final int current = ctx.skills.realLevel(Skills.PRAYER);
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
		properties.put("Time Running", Timer.format(runtime));

		if (skillData != null) {
			properties.put("TTL", Timer.format(skillData.timeToLevel(SkillData.Rate.HOUR, Skills.PRAYER)));
			properties.put("Level", String.format("%d (+%d)", current, current - startLevel.get()));
			properties.put("XP Gained", String.format("%,d", experience()));
			properties.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.PRAYER)));
			if (current < 99) {
				properties.put("XP to level " + (current + 1), String.format("%,d", SkillData.getExperenceToNextLevel(ctx, Skills.PRAYER)));
			}
		}

		final float time = runtime / 3600000f;
		properties.put("Bones Offered", String.format("%,d (%,d/h)", options.bonesOffered.get(), (int) (options.bonesOffered.get() / time)));

		if (options.useBOB.get()) {
			properties.put("Finished using BoB", options.usedBOB.get());
		}

		properties.put("Offered this trip", options.bonesOfferedThisTrip.get() + " / " + options.bonesTakenFromBank.get());

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
		String message = messageEvent.text();

		switch (messageEvent.type()) {
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
				}
				break;
			case 2:
			case 9:
				if (options.detectHouses.get()) {
					//log.info("Chat[2]: " + message);
					houseHandler.parseHouses(message);
				}
				break;
			case 4:
				if (message.equals("That player is offline, or has privacy mode enabled.")) {
					housePortal.enteringHouse.set(false);
					log.info(options.status = "Can't enter friends house anymore");

					ctx.controller.offer(new Task<LogGildedAltar>(this) {
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
					options.TimeLastOffering.set(System.currentTimeMillis());
					options.bonesOffered.incrementAndGet();
					if (options.bonesOfferedThisTrip.incrementAndGet() >= options.bonesTakenFromBank.get()) {
						options.usedBOB.set(true);
					}
				}
		}
	}

	@Override
	public void repaint(Graphics g) {
		super.repaint(g);

		if (options.newVersionAvailable.get()) {
			final Point point = ctx.input.getLocation();
			int x = point.x + 10;
			int y = point.y + 6;
			g.setColor(Color.BLACK);
			g.drawString(NEW_VERSION_STRING, x + 1, y + 1);
			g.setColor(Color.RED);
			g.drawString(NEW_VERSION_STRING, x, y);
		}


		/*final Tile location = ctx.players.local().tile();
		LogicailArea area = new LogicailArea(location.derive(-8, -8), location.derive(8, 8));
		final Tile tile = area.findSpace(ctx, 3, 3);
		tile.getMatrix(ctx).draw(g);*/

//		if (altarTask != null) {
//			final GameObject altar = altarTask.getAltar();
//			if (altar.valid()) {
//				altar.tile().getMatrix(ctx).draw(g);
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

//		if (summoningTask != null) {
//			for (Node<LogGildedAltar> node : summoningTask.getNodes()) {
//				if (node instanceof NodePath) {
//					final LocationAttribute location = ((NodePath) node).getPath().tile();
//					for (Tile tile : location.getObeliskArea().getTileArray()) {
//						tile.getMatrix(ctx).draw(g);
//					}
//				}
//			}
//		}
	}

	@Override
	public void start() {
		ctx.controller.offer(new Task<LogGildedAltar>(this) {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							gui = new LogGildedAltarGUI(LogGildedAltar.this);
						} catch (Exception exception) {
							exception.printStackTrace();
							ctx.controller.stop();
						}
					}
				});
			}
		});
	}
}
