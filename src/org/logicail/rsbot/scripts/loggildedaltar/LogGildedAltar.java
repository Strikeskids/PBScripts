package org.logicail.rsbot.scripts.loggildedaltar;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.framework.tasks.impl.AntiBan;
import org.logicail.rsbot.scripts.loggildedaltar.gui.LogGildedAltarGUI;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.BankingTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.HouseHandler;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.HouseTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.SummoningTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house.HousePortal;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille.YanilleLodestone;
import org.logicail.rsbot.util.LinkedProperties;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
	public SummoningTask summoningTask = new SummoningTask(this);
	public HouseHandler houseHandler = new HouseHandler(this);
	public BankingTask bankingTask = new BankingTask(this);
	public HouseTask houseTask = new HouseTask(this);
	public HousePortal housePortal = new HousePortal(this);

	public YanilleLodestone yanilleLodestone = new YanilleLodestone(this);

	public void createTree(LinkedList<Task> houseNodes, LinkedList<Task> bankNodes) {
		submit(new AnimationMonitor<LogGildedAltar>(this));
		submit(new AntiBan<LogGildedAltar>(this));
	}

	@Override
	public LinkedProperties getPaintInfo() {
		final LinkedProperties properties = new LinkedProperties();

		properties.put("", "");

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
					if (options.useAura) {
						submit(new Task<LogGildedAltar>(this) {
							@Override
							public void run() {
								//ActivateAura.activatedAura(); // TODO
							}

							@Override
							public boolean activate() {
								return false;
							}
						});
					}
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

								@Override
								public boolean activate() {
									return true;
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

						@Override
						public boolean activate() {
							return true;
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
						}
					}
				});
			}

			@Override
			public boolean activate() {
				return true;
			}
		});
	}
}
