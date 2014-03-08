package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/01/14
 * Time: 14:37
 */
public class IBank extends Bank {
	public static final int WIDGET_BANK_ITEMS = 54;
	private static final int WIDGET_BOUNDS = 48;

	/**
	 * Backpack when bank is open
	 */
	public final IItemStore backpack;

	public IBank(IMethodContext ctx) {
		super(ctx);
		backpack = new IItemStore(ctx, ctx.widgets.get(WIDGET, WIDGET_BANK_ITEMS));
	}

	public Component getWidget() {
		return ctx.widgets.get(WIDGET, WIDGET_BOUNDS);
	}

	public boolean isInsertMode() {
		return ctx.settings.get(161, 1) == 1;
	}

	public boolean setSwapMode(final boolean insert) {
		return isInsertMode() == insert || (ctx.widgets.get(762, 8).click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return isInsertMode() == insert;
			}
		}));
	}


	public ArrayList<ArrayList<Item>> getBankTabs() {
		if (!ctx.bank.isOpen() || (ctx.bank.getCurrentTab() != 0 && !ctx.bank.setCurrentTab(0))) {
			return null;
		}

		ArrayList<ArrayList<Item>> tabs = new ArrayList<ArrayList<Item>>();
		for (BankTab tab : BankTab.values()) {
			final ItemQuery<Item> items = getItemsInTab(tab);
			final int count = items.count();
			if (count == 0) {
				break;
			}

			ArrayList<Item> list = new ArrayList<Item>(count);
			for (Item item : items) {
				list.add(item);
			}

			tabs.add(list);
		}

		return tabs;
	}

	public int getNumberOfTabs() {
		int count = 0;
		for (int i : getTabCount()) {
			if (i > 0) {
				count++;
			}
		}

		return count;
	}

	private static final int[][] TAB_COUNT = {
			{108, 0}, {108, 10}, {108, 20},
			{109, 0}, {109, 10}, {109, 20},
			{110, 0}, {110, 10},
	};

	public ItemQuery<Item> getItemsInTab(BankTab tab) {
		final int[] count = getTabCount();

		switch (tab) {
			case NONE:
				int total = 0;
				for (Integer i : count) {
					total += i;
				}
				return skip(select(), total);
			default:
				int skip = 0;
				int take = 0;
				int x = 0;

				while (x < count.length) {
					take = count[x];
					if (x == tab.getI()) {
						break;
					}
					skip += count[x];
					x++;
				}

				return skip(select(), skip).limit(take);
		}
	}

	public static ItemQuery<Item> skip(ItemQuery<Item> query, int count) {
		for (int i = 0; i < count; i++) {
			query.poll();
		}

		return query;
	}

	private int[] getTabCount() {
		int[] count = new int[TAB_COUNT.length];

		for (int i = 0; i < TAB_COUNT.length; i++) {
			int[] setting = TAB_COUNT[i];
			count[i] = ctx.settings.get(setting[0], setting[1], 0x3ff);
		}

		return count;
	}

	public enum BankTab {
		NONE(-1, 37),
		TWO(0, 35),
		THREE(1, 33),
		FOUR(2, 31),
		FIVE(3, 29),
		SIX(4, 27),
		SEVEN(5, 25),
		EIGHT(6, 23),
		NINE(7, 21);
		private final int i;
		private final int bankWidget;

		BankTab(int i, int bankWidget) {
			this.i = i;
			this.bankWidget = bankWidget;
		}

		public boolean exists(IMethodContext ctx) {
			if (this == NONE) {
				return true;
			}
			return ctx.bank.getNumberOfTabs() >= ordinal();
		}

		public Component getWidget(IMethodContext ctx) {
			return ctx.widgets.get(Bank.WIDGET, bankWidget);
		}

		public int getI() {
			return i;
		}
	}
}
