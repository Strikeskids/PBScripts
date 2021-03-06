package org.logicail.rsbot.scripts.rt6.bankorganiser.tasks;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.IBank;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.rt6.bankorganiser.LogBankOrganiser;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt6.Bank;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Item;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/03/14
 * Time: 12:43
 */
public class MoveToTabTask extends Node<LogBankOrganiser> {
	private final List<LinkedHashSet<Integer>> mapping;

	public List<LinkedHashSet<Integer>> getMapping() {
		return mapping;
	}

	public MoveToTabTask(LogBankOrganiser script, List<LinkedHashSet<Integer>> mapping) {
		super(script);
		this.mapping = mapping;
	}

	@Override
	public String toString() {
		return "MoveToTabTask";
	}

	private static final IBank.BankTab[] BANK_TABS = IBank.BankTab.values();

	@Override
	public boolean valid() {
		if (!removedInvalidTabs || mapping.size() < ctx.bank.getNumberOfTabs() + 1) {
			return true;
		}
		for (int i = 0; i < mapping.size(); i++) {
			final LinkedHashSet<Integer> set = mapping.get(i);
			final IBank.BankTab sortingTab = BANK_TABS[i];
			final HashSet<Integer> alreadyHave = new HashSet<Integer>();

			for (Item item : ctx.bank.getItemsInTab(sortingTab)) {
				alreadyHave.add(item.id());
			}

			if (sortingTab != IBank.BankTab.ONE) {
				for (Integer integer : alreadyHave) {
					if (!set.contains(script.itemCategoriser.id(integer))) {
						// Remove
						final Item item = ctx.bank.getItemsInTab(sortingTab).id(integer).poll();
						if (item.valid()) {
							return true;
						}
					}
				}
			}

			if (!ctx.bank.select().select(new Filter<Item>() {
				@Override
				public boolean accept(Item item) {
					return set.contains(script.itemCategoriser.id(item.id())) && !alreadyHave.contains(item.id());
				}
			}).isEmpty()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void run() {
		cleanUpMapping();

		for (int i = 0; i < mapping.size(); i++) {
			final LinkedHashSet<Integer> set = mapping.get(i);
			final IBank.BankTab sortingTab = BANK_TABS[i];
			final HashSet<Integer> alreadyHave = new HashSet<Integer>();

			for (Item item : ctx.bank.getItemsInTab(sortingTab)) {
				alreadyHave.add(item.id());
			}

			if (sortingTab != IBank.BankTab.ONE) {
				for (Integer integer : alreadyHave) {
					if (!set.contains(script.itemCategoriser.id(integer))) {
						// Remove
						final Item item = ctx.bank.getItemsInTab(sortingTab).id(integer).poll();
						if (item.valid()) {
							//System.out.println("Remove from tab");
							script.status = "Remove '" + item.name() + "' from tab";
							move(item, IBank.BankTab.ONE);
							Condition.sleep(100);
							return;
						}
					}
				}
			}

			for (Item item : ctx.bank.select().select(new Filter<Item>() {
				@Override
				public boolean accept(Item item) {
					return set.contains(script.itemCategoriser.id(item.id())) && !alreadyHave.contains(item.id());
				}
			}).sort(script.itemCategoriser.getSorter())) {
				move(item, sortingTab);
				Condition.sleep(66);
				return;
			}
		}
	}

	private boolean removedInvalidTabs = false;

	public void cleanUpMapping() {
		// Remove tabs you have no items for
		if (!removedInvalidTabs) {
			int i = 0;
			final Iterator<LinkedHashSet<Integer>> iterator = mapping.iterator();
			while (iterator.hasNext()) {
				final LinkedHashSet<Integer> next = iterator.next();
				if (i > 0) {
					if (ctx.bank.select().select(new Filter<Item>() {
						@Override
						public boolean accept(Item item) {
							return next.contains(script.itemCategoriser.id(item.id()));
						}
					}).count() < 1) {
						iterator.remove();
						//System.out.println("Remove tab " + i + " no matching items");
					}
				}
				i++;
			}
			removedInvalidTabs = true;
		}
	}

	private IBank.BankTab getNextTab() {
		for (IBank.BankTab tab : BANK_TABS) {
			if (!tab.exists(ctx)) {
				//System.out.println("getNextTab " + tab);
				return tab;
			}
		}
		return IBank.BankTab.NINE;
	}

	/**
	 * Move item to tab
	 *
	 * @param item
	 * @param tab
	 */
	private boolean move(final Item item, final IBank.BankTab tab) {
		script.status = "Move '" + item.name() + "' to tab " + (tab.ordinal() + 1);
		Component destination = tab.getWidget(ctx);

		if (!destination.valid()) {
			return false;
		}

		if (destination.itemId() == -1) {
			script.status = "Create tab " + (ctx.bank.getNumberOfTabs() + 1);
		}

		final Component value = this.ctx.widgets.component(Bank.WIDGET, Bank.COMPONENT_CONTAINER_ITEMS);
		if (!value.valid() || !item.valid()) {
			script.status = "Scroll failed 1";
			return false;
		}
		final Component component = item.component();
		if (component.relativePoint().y == 0 && !ctx.bank.currentTab(0) && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return component.relativePoint().y != 0;
			}
		}, 50, 20)) {
			script.status = "Scroll failed 2";
			return false;
		}
		final Rectangle viewportRect = value.viewportRect();
		if (!viewportRect.contains(component.viewportRect()) && !this.ctx.widgets.scroll(component, this.ctx.widgets.component(Bank.WIDGET, Bank.COMPONENT_SCROLL_BAR), true)) {
			script.status = "Scroll failed 3";
			return false;
		}

		if (item.hover()) {
			try {
				ctx.input.press(1);
				ctx.sleep(50);
				destination.hover();
				ctx.sleep(50);
			} finally {
				ctx.input.release(1);
				ctx.sleep(50);
			}
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !item.valid();
				}
			}, 50, 20)) {
				return true;
			}
		}

		return false;
	}
}
