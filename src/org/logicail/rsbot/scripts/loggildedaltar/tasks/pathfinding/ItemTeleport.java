package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.methods.Equipment;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 17:41
 */
public class ItemTeleport extends NodePath {
	protected final int[] ids;
	protected final String destination;
	protected final Equipment.Slot slot;

	public ItemTeleport(LogGildedAltar script, Path path, String destination, Equipment.Slot slot, int... ids) {
		super(script, path);
		this.ids = ids;
		this.destination = destination;
		this.slot = slot;
	}

	@Override
	public String toString() {
		return "ItemTeleport{" +
				"ids=" + Arrays.toString(ids) +
				", destination='" + destination + '\'' +
				'}';
	}

	@Override
	protected boolean doLarge() {
		if (ctx.equipment.select().id(ids).isEmpty() && !ctx.backpack.select().id(ids).isEmpty()) {
			if (!ctx.equipment.getItemAt(slot).isValid()) {
				ctx.equipment.equip(ids);
			}
		}

		return useItemTeleport(ctx, destination, 12, ids);
	}

	/**
	 * @param ctx
	 * @param destination
	 * @param tries       (600 * tries) ms
	 * @param ids
	 * @return
	 */
	public static boolean useItemTeleport(IMethodContext ctx, String destination, int tries, int... ids) {
		// TODO Check action bar

		if (ctx.hud.isVisible(Hud.Window.WORN_EQUIPMENT)) {
			return itemTeleport(ctx, ctx.equipment.select().id(ids), Hud.Window.WORN_EQUIPMENT, destination, tries)
					|| itemTeleport(ctx, ctx.backpack.select().id(ids), Hud.Window.BACKPACK, destination, tries);
		}

		return itemTeleport(ctx, ctx.backpack.select().id(ids), Hud.Window.BACKPACK, destination, tries)
				|| itemTeleport(ctx, ctx.equipment.select().id(ids), Hud.Window.WORN_EQUIPMENT, destination, tries);
	}

	private static boolean itemTeleport(final IMethodContext ctx, ItemQuery<Item> query, Hud.Window window, String destination, int tries) {
		for (Item item : query.first()) {
			destination = destination.toLowerCase();

			if (!ctx.hud.isVisible(window)) {
				ctx.hud.view(window);
				ctx.game.sleep(100, 600);
			}

			final Tile startLocation = ctx.players.local().getLocation();
			final TeleportSucceeded teleportSucceeded = new TeleportSucceeded(ctx, startLocation);
			for (String action : item.getActions()) {
				if (action == null) continue;
				if (action.toLowerCase().contains(destination)) {
					if (item.interact(action, item.getName())) {
						ctx.game.sleep(100, 600);
						return Condition.wait(teleportSucceeded, 600, tries);
					}
					break;
				} else if (action.startsWith("Teleport") || action.startsWith("Rub")) {
					if (item.interact(action, item.getName())) {
						final String finalDestination = destination;
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.chat.select().text(finalDestination).isEmpty(); // TODO TeleportOption
							}
						})) {
							for (ChatOption option : ctx.chat.select().text(finalDestination).first()) {
								if (option.select(Random.nextBoolean())) {
									return Condition.wait(teleportSucceeded, 600, tries);
								}
							}
						}
					}
					break;
				}
			}
		}

		return false;
	}

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		List<BankRequiredItem> list = new ArrayList<BankRequiredItem>();

		if (ctx.equipment.select().id(ids).isEmpty() && ctx.backpack.select().id(ids).isEmpty()) {
			if (ctx.bank.isOpen()) {
				final Item item = ctx.bank.select().id(ids).sort(new Comparator<Item>() {
					@Override
					public int compare(Item o1, Item o2) {
						return Integer.compare(o1.getId(), o2.getId());
					}
				}).reverse().poll();

				if (item.isValid()) {
					list.add(new BankRequiredItem(1, true, slot, item.getId()));
				}
			}
			list.add(new BankRequiredItem(1, true, slot, ids));
		}

		return list;
	}
}
