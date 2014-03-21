package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Equipment;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Collections;
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

	/**
	 * @param ctx
	 * @param destination
	 * @param tries       (600 * tries) ms
	 * @param ids
	 * @return
	 */
	public static boolean useItemTeleport(IMethodContext ctx, String destination, int tries, int... ids) {
		if (itemTeleport(ctx, ctx.combatBar.select().id(ids).poll(), destination, tries)) {
			return true;
		}

		if (ctx.hud.isVisible(Hud.Window.WORN_EQUIPMENT)) {
			return itemTeleport(ctx, ctx.equipment.select().id(ids).poll(), Hud.Window.WORN_EQUIPMENT, destination, tries)
					|| itemTeleport(ctx, ctx.backpack.select().id(ids).poll(), Hud.Window.BACKPACK, destination, tries);
		}

		return itemTeleport(ctx, ctx.backpack.select().id(ids).poll(), Hud.Window.BACKPACK, destination, tries)
				|| itemTeleport(ctx, ctx.equipment.select().id(ids).poll(), Hud.Window.WORN_EQUIPMENT, destination, tries);
	}

	private static boolean itemTeleport(final IMethodContext ctx, Action actionItem, String destination, int tries) {
		if (actionItem.getType() != Action.Type.ITEM) {
			return false;
		}

		final Tile startLocation = ctx.players.local().getLocation();
		final TeleportSucceeded teleportSucceeded = new TeleportSucceeded(ctx, startLocation);
		final Item item = new Item(ctx, actionItem.getComponent());

		final ArrayList<String> actions = new ArrayList<String>();
		final String[] groundActions = item.getGroundActions();
		if (groundActions != null) {
			Collections.addAll(actions, groundActions);
		}
		final String[] itemActions = item.getActions();
		if (itemActions != null) {
			Collections.addAll(actions, itemActions);
		}

		if (actions.contains("Operate")) {
			if (actionItem.select(true)) {
				if (chatDestination(ctx, destination)) {
					return Condition.wait(teleportSucceeded, 600, tries);
				}
			}
			return false;
		}

		for (final String action : actions) {
			if (action == null) {
				continue;
			}

			if (action.startsWith("Teleport") || action.startsWith("Rub")) {
				return ctx.combatBar.setExpanded(true) && item.interact(new Filter<Menu.Entry>() {
					@Override
					public boolean accept(Menu.Entry entry) {
						return entry.action.equals(action) || entry.action.equals("Operate");
					}
				}) && chatDestination(ctx, destination) && Condition.wait(teleportSucceeded, 600, tries);
			}
		}

		return false;
	}

	private static boolean chatDestination(final IMethodContext ctx, final String destination) {
		Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !ctx.chat.select().isEmpty();
			}
		}, 300, Random.nextInt(10, 15));

		return ctx.chat.select().select(new Filter<ChatOption>() {
			@Override
			public boolean accept(ChatOption chatOption) {
				return chatOption.getText().contains(destination);
			}
		}).poll().select(Random.nextBoolean());
	}

	private static boolean itemTeleport(final IMethodContext ctx, final Item item, Hud.Window window, String destination, int tries) {
		final String lowercase = destination.toLowerCase();

		if (!ctx.hud.isVisible(window)) {
			ctx.hud.view(window);
			ctx.game.sleep(100, 600);
		}

		if (!item.isValid()) {
			return false;
		}

		if (window == Hud.Window.BACKPACK && !ctx.backpack.scroll(item)) {
			return false;
		}

		final Tile startLocation = ctx.players.local().getLocation();
		final TeleportSucceeded teleportSucceeded = new TeleportSucceeded(ctx, startLocation);

		if (item.contains(ctx.mouse.getLocation()) || item.hover()) {
			final Filter<Menu.Entry> filter = new Filter<Menu.Entry>() {
				@Override
				public boolean accept(Menu.Entry entry) {
					return entry.action.toLowerCase().contains(lowercase) && entry.option.contains(item.getName());
				}
			};

			if (ctx.menu.indexOf(filter) > -1) {
				return item.interact(filter) && Condition.wait(teleportSucceeded, 600, tries);
			} else {
				final Filter<Menu.Entry> filterTeleportRub = new Filter<Menu.Entry>() {
					@Override
					public boolean accept(Menu.Entry entry) {
						return (entry.action.startsWith("Teleport") || entry.action.startsWith("Rub")) && entry.option.contains(item.getName());
					}
				};

				if (ctx.menu.indexOf(filterTeleportRub) > -1) {
					if (window == Hud.Window.BACKPACK && !ctx.backpack.scroll(item)) {
						return false;
					}
					if (item.interact(filterTeleportRub)) {
						final String finalDestination = destination;
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.chat.select().text(finalDestination).isEmpty(); // TODO TeleportOption
							}
						}, Random.nextInt(200, 500), Random.nextInt(10, 15))) {
							for (ChatOption option : ctx.chat.select().text(finalDestination).first()) {
								if (option.select(Random.nextBoolean())) {
									return Condition.wait(teleportSucceeded, 600, tries);
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return destination;
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

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		List<BankRequiredItem> list = new ArrayList<BankRequiredItem>();

		if (ctx.equipment.select().id(ids).isEmpty() && ctx.backpack.select().id(ids).isEmpty()) {
			if (ctx.bank.isOpen()) {
				final Item item = ctx.bank.select().id(ids).sort(new Comparator<Item>() {
					@Override
					public int compare(Item o1, Item o2) {
						return Integer.valueOf(o1.getId()).compareTo(o2.getId());
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
