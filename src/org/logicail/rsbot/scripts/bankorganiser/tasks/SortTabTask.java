package org.logicail.rsbot.scripts.bankorganiser.tasks;

import org.logicail.rsbot.scripts.bankorganiser.ItemData;
import org.logicail.rsbot.scripts.bankorganiser.LogBankOrganiser;
import org.logicail.rsbot.scripts.framework.context.providers.IBank;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
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
	public boolean isValid() {
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

		// extra for tab0
		// Categorys then rest sorted alphabetically

		// Set bank to insert mode

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
				sorted.add(item.getId());
			}

			int j = 0;

			for (Item item : unsorted) {
				final int index = sorted.indexOf(item.getId());
				if (index != j) {
					final Integer id = sorted.get(j);
					final Item destination = ctx.bank.getItemsInTab(tab).id(id).poll();

					swap(item, destination);

					return;
				}
				j++;
			}
		}

		script.getController().stop();
	}

	private boolean swap(final Item item, Item d) {
		script.status = "Swap '" + item.getName() + "' and " + d.getName();
		final int id = item.getId();

		Component destination = d.getComponent();

		if (!destination.isValid()) {
			return false;
		}

		final Component value = this.ctx.widgets.get(Bank.WIDGET, Bank.COMPONENT_CONTAINER_ITEMS);
		if (!value.isValid() || !item.isValid()) {
			return false;
		}
		final Component component = item.getComponent();
		if (component.getRelativeLocation().y == 0 && !ctx.bank.setCurrentTab(0) && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return component.getRelativeLocation().y != 0;
			}
		}, 200, 10)) {
			return false;
		}
		final Rectangle viewportRect = value.getViewportRect();
		if (!viewportRect.contains(component.getViewportRect()) && !this.ctx.widgets.scroll(component, this.ctx.widgets.get(762, 40), viewportRect.contains(this.ctx.mouse.getLocation()))) {
			return false;
		}

		if (!ctx.mouse.drag(item.getNextPoint(), destination.getNextPoint(), true) || !Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return item.getComponent().getItemId() != id;
			}
		}, 250, 20)) {
			script.status = "Item did not move";
			sleep(250, 1000);
			return false;
		}

		return true;
	}

	class ItemSorter implements Comparator<Item> {
		public int size() {
			return order.size();
		}

		private final List<Integer> order;

		ItemSorter(List<Integer> order) {
			this.order = order;
		}

		@Override
		public int compare(Item lhs, Item rhs) {
			final int indexlhs = order.indexOf(ItemData.getId(lhs.getId()));
			final int indexrhs = order.indexOf(ItemData.getId(rhs.getId()));

			if (indexlhs == -1 && indexrhs == -1) {
				return lhs.getName().compareTo(rhs.getName());
			}

			return indexlhs - indexrhs;
		}
	}
}
