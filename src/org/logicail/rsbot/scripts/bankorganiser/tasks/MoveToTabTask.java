package org.logicail.rsbot.scripts.bankorganiser.tasks;

import org.logicail.rsbot.scripts.bankorganiser.ItemData;
import org.logicail.rsbot.scripts.bankorganiser.LogBankOrganiser;
import org.logicail.rsbot.scripts.framework.context.providers.IBank;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.awt.*;
import java.util.*;
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
	public boolean isValid() {
		if (!removedInvalidTabs || mapping.size() < ctx.bank.getNumberOfTabs() + 1) {
			return true;
		}
		for (int i = 0; i < mapping.size(); i++) {
			final LinkedHashSet<Integer> set = mapping.get(i);
			final IBank.BankTab sortingTab = BANK_TABS[i];
			final HashSet<Integer> alreadyHave = new HashSet<Integer>();

			for (Item item : ctx.bank.getItemsInTab(sortingTab)) {
				alreadyHave.add(item.getId());
			}

			if (sortingTab != IBank.BankTab.NONE) {
				for (Integer integer : alreadyHave) {
					if (!set.contains(ItemData.getId(integer))) {
						// Remove
						final Item item = ctx.bank.getItemsInTab(sortingTab).id(integer).poll();
						if (item.isValid()) {
							return true;
						}
					}
				}
			}

			if (!ctx.bank.select().select(new Filter<Item>() {
				@Override
				public boolean accept(Item item) {
					return set.contains(ItemData.getId(item.getId())) && !alreadyHave.contains(item.getId());
				}
			}).isEmpty()) {
				return true;
			}
		}

		return false;
	}

//	private ItemQuery<Item> getItemsInWrongTab(IBank.BankTab tab, final HashSet<Integer> correctIds) {
//		return ctx.bank.getItemsInTab(tab).select(new Filter<Item>() {
//			@Override
//			public boolean accept(Item item) {
//				return !correctIds.contains(ItemData.getId(item.getId()));
//			}
//		});
//	}

	@Override
	public void run() {
		if (!createTabs()) {
			return;
		}

		for (int i = 0; i < mapping.size(); i++) {
			final LinkedHashSet<Integer> set = mapping.get(i);
			final IBank.BankTab sortingTab = BANK_TABS[i];
			final HashSet<Integer> alreadyHave = new HashSet<Integer>();

			for (Item item : ctx.bank.getItemsInTab(sortingTab)) {
				alreadyHave.add(item.getId());
			}

			if (sortingTab != IBank.BankTab.NONE) {
				for (Integer integer : alreadyHave) {
					if (!set.contains(ItemData.getId(integer))) {
						// Remove
						final Item item = ctx.bank.getItemsInTab(sortingTab).id(integer).poll();
						if (item.isValid()) {
							//System.out.println("Remove from tab");
							script.status = "Remove '" + item.getName() + "' from tab";
							move(item, IBank.BankTab.NONE);
							sleep(100, 300);
							return;
						}
					}
				}
			}

			for (Item item : ctx.bank.select().select(new Filter<Item>() {
				@Override
				public boolean accept(Item item) {
					return set.contains(ItemData.getId(item.getId())) && !alreadyHave.contains(item.getId());
				}
			}).sort(ItemData.getSorter())) {
				move(item, sortingTab);
				sleep(100, 500);
				return;
			}
		}
	}

	boolean removedInvalidTabs = false;

	/**
	 * Setup tabs
	 */
	private boolean createTabs() {
		cleanUpMapping();

//		final int haveTabs = ctx.bank.getNumberOfTabs() + 1;
//		final int needTabs = mapping.size();
//
//		if (haveTabs < needTabs) {
//			for (int i = haveTabs; i < mapping.size(); i++) {
//				final LinkedHashSet<Integer> set = mapping.get(i);
//				final Item item = ctx.bank.select().select(new Filter<Item>() {
//					@Override
//					public boolean accept(Item item) {
//						return set.contains(ItemData.getId(item.getId()));
//					}
//				}).reverse().poll();
//				if (item.isValid()) {
//					move(item, getNextTab());
//					return ctx.bank.getNumberOfTabs() >= needTabs;
//				}
//			}
//		}

//		return ctx.bank.getNumberOfTabs() + 1 >= needTabs;
		return true;
	}

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
							return next.contains(ItemData.getId(item.getId()));
						}
					}).count() <= 1) {
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
		script.status = "Move '" + item.getName() + "' to tab " + (tab.ordinal() + 1);
		final int id = item.getId();

		Component destination = tab.getWidget(ctx);

		if (!destination.isValid()) {
			return false;
		}

		if (destination.getItemId() == -1) {
			script.status = "Create tab " + (ctx.bank.getNumberOfTabs() + 1);
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
		}, 100, 10)) {
			return false;
		}
		final Rectangle viewportRect = value.getViewportRect();
		if (!viewportRect.contains(component.getViewportRect()) && !this.ctx.widgets.scroll(component, this.ctx.widgets.get(762, 40), viewportRect.contains(this.ctx.mouse.getLocation()))) {
			return false;
		}

		if (!ctx.mouse.drag(item.getNextPoint(), destination.getNextPoint(), true) || !Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !ctx.bank.getItemsInTab(tab).id(id).isEmpty();
			}
		}, 250, 20)) {
			script.status = "Item did not move";
			sleep(250, 1000);
			return false;
		}

		return true;
	}
}
