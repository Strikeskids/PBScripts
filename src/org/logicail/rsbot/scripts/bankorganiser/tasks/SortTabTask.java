package org.logicail.rsbot.scripts.bankorganiser.tasks;

import org.logicail.rsbot.scripts.bankorganiser.LogBankOrganiser;
import org.logicail.rsbot.scripts.framework.context.providers.IBank;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt6.Bank;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/03/14
 * Time: 16:05
 */
public class SortTabTask extends Node<LogBankOrganiser> {
	private final MoveToTabTask moveToTabTask;
	private static final IBank.BankTab[] BANK_TABS = IBank.BankTab.values();
	private ItemSorter[] sorters = null;

	private List<List<Integer>> mappingList = null;

	public SortTabTask(LogBankOrganiser script, MoveToTabTask moveToTabTask) {
		super(script);
		this.moveToTabTask = moveToTabTask;
	}

	private boolean isTabSorted(IBank.BankTab tab) {
		return false;
	}

	@Override
	public boolean valid() {
		moveToTabTask.cleanUpMapping();

		if (mappingList == null) {
			mappingList = new ArrayList<List<Integer>>();
			List<LinkedHashSet<Integer>> mapping = moveToTabTask.getMapping();
			for (LinkedHashSet<Integer> set : mapping) {
				mappingList.add(new ArrayList<Integer>(set));
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "SortTabTask";
	}

	@Override
	public void run() {
		script.status = "Sort tab";

		// extra for tab0
		// Categories then rest sorted alphabetically

		for (int i = 0; i < mappingList.size(); i++) {
			if (sorters == null) {
				sorters = new ItemSorter[mappingList.size()];
			}

			final IBank.BankTab tab = BANK_TABS[i];

			ItemSorter sorter = sorters[i];
			if (sorter == null) {
				sorter = sorters[i] = new ItemSorter(script.itemData, mappingList.get(i));
			}

			ArrayList<Item> current = new ArrayList<Item>();
			ctx.bank.getItemsInTab(tab).addTo(current);
			final Iterator<Item> iterator = current.iterator();
			for (final Item item : ctx.bank.getItemsInTab(tab).sort(sorter)) {
				//script.log.info(item.id() + " " + item.name());

				if (!iterator.hasNext()) {
					return;
				}

				final Item poll = iterator.next();
				if (poll.id() != item.id()) {
					//script.log.info(poll.id() + " != " + item.id());
					final Item destination = ctx.bank.getItemsInTab(tab).id(item.id()).select(new Filter<Item>() {
						@Override
						public boolean accept(Item i) {
							return i.component().index() > poll.component().index();
						}
					}).poll();

					if (!destination.valid()) {
						continue;
					}

					script.log.info("Swap: " + swap(poll, destination));

					return;
				}
			}
		}

		ctx.controller().stop();
	}

	public boolean swap(final Item item, Item destination) {
		script.status = "Swap '" + item.name() + "' and '" + destination.name() + "'";
		//script.log.info(script.status);

		final int id = item.id();

		Component destinationComponent = destination.component();

		if (!destinationComponent.valid()) {
			return false;
		}

		final Component bankContainer = ctx.widgets.component(Bank.WIDGET, Bank.COMPONENT_CONTAINER_ITEMS);
		if (!bankContainer.valid() || !item.valid()) {
			return false;
		}
		final Component itemComponent = item.component();
		if (itemComponent.relativePoint().y == 0 && !ctx.bank.currentTab(0) && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return itemComponent.relativePoint().y != 0;
			}
		}, 50, 20)) {
			return false;
		}
		final Rectangle viewportRect = bankContainer.viewportRect();
		if (!viewportRect.contains(itemComponent.viewportRect()) && !ctx.widgets.scroll(itemComponent, ctx.widgets.component(Bank.WIDGET, Bank.COMPONENT_SCROLL_BAR), true)) {
			return false;
		}

		// Got to handle destination not on screen
		if (item.hover()) {
			ctx.sleep(100);
			try {
				if (ctx.mouse.press(1)) {
					ctx.sleep(50);
					// scroll

					if (scroll(item, destination)) {
						ctx.sleep(50);
						destination.hover();
						ctx.sleep(50);
					} else {
						// move outside bank
					}
				}
			} catch (Exception e) {
			} finally {
				ctx.mouse.release(1);
				ctx.sleep(50);
			}
			return Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !item.valid();
				}
			}, 50, 20);
		}

		script.log.info("Can't move item, not both in view!");
		return false;
	}

	private boolean scroll(final Item item, Item destination) {
		if (!ctx.bank.opened()) {
			return false;
		}

		final Component bankContainer = ctx.widgets.component(Bank.WIDGET, Bank.COMPONENT_CONTAINER_ITEMS);

		int tries = 0;
		while (tries < 100) {
			if (!ctx.bank.opened()) {
				return false;
			}
			if (!bankContainer.viewportRect().contains(destination.component().viewportRect())) {
				if (!bankContainer.viewportRect().contains(ctx.mouse.getLocation())) {
					bankContainer.hover();
				}
				ctx.mouse.scroll(destination.component().relativePoint().y > item.component().relativePoint().y);
				ctx.sleep(100);
			} else {
				break;
			}
			tries++;
		}

		return bankContainer.viewportRect().contains(destination.component().viewportRect());
	}
}
