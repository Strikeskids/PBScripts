package org.logicail.rsbot.scripts.bankorganiser.tasks;

import org.logicail.rsbot.scripts.bankorganiser.LogBankOrganiser;
import org.logicail.rsbot.scripts.framework.context.providers.IBank;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.Bank;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ItemQuery;

import java.awt.*;
import java.util.ArrayList;
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

	@Override
	public boolean valid() {
		return true;
	}

	@Override
	public String toString() {
		return "SortTabTask";
	}

	@Override
	public void run() {
		script.status = "Sort tab";

		moveToTabTask.cleanUpMapping();

		if (mappingList == null) {
			mappingList = new ArrayList<List<Integer>>();
			List<LinkedHashSet<Integer>> mapping = moveToTabTask.getMapping();
			for (LinkedHashSet<Integer> set : mapping) {
				mappingList.add(new ArrayList<Integer>(set));
			}
		}

		// Set bank to insert mode
		if (!ctx.bank.setSwapMode(true)) {
			return;
		}

		// extra for tab0
		// Categorys then rest sorted alphabetically


		for (int i = 0; i < mappingList.size(); i++) {
			if (sorters == null) {
				sorters = new ItemSorter[mappingList.size()];
			}

			final IBank.BankTab tab = BANK_TABS[i];

			ItemSorter sorter = sorters[i];
			if (sorter == null) {
				sorter = sorters[i] = new ItemSorter(mappingList.get(i));
			}

			final ItemQuery<Item> items = ctx.bank.getItemsInTab(tab);
			final List<Item> unsorted = new ArrayList<Item>(items.size());
			for (Item item : items) {
				unsorted.add(item);
			}

			final ItemQuery<Item> sort = items.sort(sorter);
			final List<Integer> sorted = new ArrayList<Integer>(sort.size());
			for (Item item : sort) {
				sorted.add(item.id());
			}

			int j = 0;

			for (Item item : unsorted) {
				final int index = sorted.indexOf(item.id());
				if (index != j) {
					final Integer id = sorted.get(j);
					final Item destination = ctx.bank.getItemsInTab(tab).id(id).poll();

					swap(item, destination);

					return;
				}
				j++;
			}
		}

		ctx.controller().stop();
	}

	private boolean swap(final Item item, Item destination) {
		script.status = "Swap '" + item.name() + "' and '" + destination.name() + "'";

		final int id = item.id();

		org.powerbot.script.rt6.Component destinationComponent = destination.component();

		if (!destinationComponent.valid()) {
			return false;
		}

		final org.powerbot.script.rt6.Component bankContainer = ctx.widgets.component(Bank.WIDGET, Bank.COMPONENT_CONTAINER_ITEMS);
		if (!bankContainer.valid() || !item.valid()) {
			return false;
		}
		final org.powerbot.script.rt6.Component itemComponent = item.component();
		if (itemComponent.relativePoint().y == 0 && !ctx.bank.currentTab(0) && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return itemComponent.relativePoint().y != 0;
			}
		}, 200, 10)) {
			return false;
		}
		final Rectangle viewportRect = bankContainer.viewportRect();
		if (!viewportRect.contains(itemComponent.viewportRect()) && !ctx.widgets.scroll(itemComponent, ctx.widgets.component(Bank.WIDGET, Bank.COMPONENT_SCROLL_BAR), viewportRect.contains(ctx.mouse.getLocation()))) {
			return false;
		}

		if (!viewportRect.contains(destinationComponent.viewportRect())) {
			// Got to handle destination not on screen
			System.out.println("Can't move item, not both in view!");
			return false;
		}

		if (item.hover() && !ctx.mouse.drag(destinationComponent.nextPoint(), true) || !Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return item.component().itemId() != id;
			}
		}, 250, 20)) {
			script.status = "Item did not move";
			sleep(333);
			return false;
		}

		return true;
	}
}
