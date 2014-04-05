package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.util.Timer;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Interactive;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ItemQuery;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/12/13
 * Time: 19:01
 */
public class ISkillingInterface extends ItemQuery<Item> {
	private static final int WIDGET_MAIN = 1370;
	private static final int WIDGET_MAIN_ACTION = 40;
	private static final int WIDGET_MAIN_ACTION_TOOLTIP = 37;
	private static final int WIDGET_MAIN_QUANTITY = 74;
	private static final int WIDGET_INTERFACE_MAIN = 1371;
	private static final int WIDGET_INTERFACE_MAIN_ITEMS = 44;
	private static final int WIDGET_INTERFACE_CATEGORY = 51;
	private static final int WIDGET_MAIN_CLOSE_BUTTON = 30;
	private static final int WIDGET_QUANTITY_INCREASE = 29;
	private static final int WIDGET_QUANTITY_DECREASE = 34;
	private static final int WIDGET_SCROLLBAR_PARENT = 47;
	private static final int WIDGET_PRODUCTION_MAIN = 1251;
	private static final int WIDGET_PRODUCTION_PROGRESS = 33;
	private static final int WIDGET_PRODUCTION_CANCEL = 48;
	private static final int WIDGET_INTERFACE_MAIN_CURRENT_CATEGORY = 51;
	protected final IClientContext ctx;

	private int indexSelectedItem = -1;


	public ISkillingInterface(IClientContext context) {
		super(context);
		ctx = context;
	}

	public boolean cancelProduction() {
		if (isProductionInterfaceOpen()) {
			if (isProductionComplete()) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !isProductionInterfaceOpen();
					}
				})) {
					return true;
				}
			}

			if (ctx.widgets.component(WIDGET_PRODUCTION_MAIN, WIDGET_PRODUCTION_CANCEL).interact("Cancel") && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !isProductionInterfaceOpen();
				}
			})) {
				ctx.sleep(250);
			}
		}

		return !isProductionInterfaceOpen();
	}

	public boolean isProductionComplete() {
		final Component component = ctx.widgets.component(WIDGET_PRODUCTION_MAIN, WIDGET_PRODUCTION_PROGRESS);

		if (component.valid() && component.visible()) {
			final String[] split = component.text().split("/");
			return split.length == 2 && split[0].equals(split[1]);
		}

		return false;
	}

	public boolean isProductionInterfaceOpen() {
		Component component = ctx.widgets.component(WIDGET_PRODUCTION_MAIN, 5);
		return component.valid() && component.visible();
	}

	public boolean close() {
		if (!opened()) {
			return true;
		}

		/*if (Random.nextBoolean()) {
			ctx.keyboard.send("{VK_ESCAPE down}");
			ctx.keyboard.send("{VK_ESCAPE up}");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !opened();
				}
			});
		} else*/
		{
			Component closeButton = ctx.widgets.component(WIDGET_MAIN, WIDGET_MAIN_CLOSE_BUTTON);
			if (closeButton.valid() && closeButton.interact("Close")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !opened();
					}
				});
			}
		}

		return !opened();
	}

	public boolean opened() {
		return ctx.widgets.component(WIDGET_MAIN, 0).valid();
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new LinkedList<Item>();

		final Component[] children = ctx.widgets.component(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_MAIN_ITEMS).components();
		if (children != null && children.length > 0) {
			for (Component child : children) {
				if (child.itemId() != -1) {
					items.add(new Item(ctx, child));
				}
			}
		}

		return items;
	}

	public String getAction() {
		try {
			if (opened()) {
				return ctx.widgets.component(WIDGET_MAIN, WIDGET_MAIN_ACTION).component(0).text();
			}
		} catch (Exception ignored) {
		}

		return "Unknown";
	}

	public Item getSelectedItem() {
		if (!opened()) {
			return nil();
		}

		if (indexSelectedItem > -1) {
			Component component = ctx.widgets.component(WIDGET_MAIN, indexSelectedItem);
			return component.valid() ? new Item(ctx, component) : nil();
		}

		for (Component component : ctx.widgets.widget(WIDGET_MAIN).components()) {
			if (component.itemId() > -1) {
				indexSelectedItem = component.index();
				return component.valid() ? new Item(ctx, component) : nil();
			}
		}

		return nil();
	}

	@Override
	public Item nil() {
		return ctx.backpack.nil();
	}

	public boolean select(final String categoryString, final int itemId) {
		return select(getCategoryIndexOf(new Filter<String>() {
			@Override
			public boolean accept(String s) {
				return s.equalsIgnoreCase(categoryString);
			}
		}), itemId);
	}

	public int getCategoryIndexOf(Filter<String> filter) {
		final String[] categorys = getCategorys();

		for (int i = 0; i < categorys.length; i++) {
			String category = categorys[i];
			if (filter.accept(category)) {
				return i;
			}
		}

		return 0;
	}

	public String[] getCategorys() {
		List<String> list = new LinkedList<String>();

		final Component[] options = ctx.widgets.component(WIDGET_INTERFACE_MAIN, 62).components();

		for (Component option : options) {
			if (option.valid()) {
				final String text = option.text();
				if (!text.isEmpty()) {
					list.add(text);
				}
			}
		}

		return list.toArray(new String[list.size()]);
	}

	public boolean select(final int categoryIndex, final int itemId) {
		//ctx.log.info(String.format("ISkillingInterface %d %d", categoryIndex, itemId));
		if (!opened()) {
			return false;
		}

		if (getSelectedItem().id() == itemId) {
			return true;
		}

		setCategory(categoryIndex);

		if (Random.nextBoolean() && Random.nextBoolean()) {
			final Item poll = select().select(Interactive.areInViewport()).shuffle().poll();
			if (poll.valid() && poll.interact("Select")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return getSelectedItem().id() == poll.id();
					}
				}, 150, 8);
			}
		}


		final Item poll = select().id(itemId).poll();
		if ((poll.inViewport() || ctx.widgets.scroll(poll.component(), ctx.widgets.component(WIDGET_INTERFACE_MAIN, WIDGET_SCROLLBAR_PARENT), true)) && poll.interact("Select")) {
			return Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return getSelectedItem().id() == itemId;
				}
			}, 150, 8);
		}

		if (getSelectedItem().id() == itemId) {
			return true;
		}

		ctx.script.log.info("Failed to select item " + itemId + " selected " + getSelectedItem().id());

		return false;
	}

	private boolean setCategory(int index) {
		final String currentCategory = getCategory();
		final String[] categorys = getCategorys();

		if (categorys.length > index) {
			final String target = categorys[index];
			if (currentCategory.equalsIgnoreCase(target)) {
				return true;
			}

			Component child = ctx.widgets.component(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_CATEGORY).component(0);
			if (child.valid() && child.click(true)) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return getCategoryRectangle(ctx, target) != null;
					}
				})) {
					ctx.sleep(400);
					Component component = getCategoryRectangle(ctx, target);
					if (component != null && component.interact("Select")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !currentCategory.equalsIgnoreCase(getCategory());
							}
						}, 200, 10);
						ctx.sleep(400);
					}
				}
			}

			return getCategory().equalsIgnoreCase(target);
		}


		// Assume true for only one category
		return true;
	}

	public String getCategory() {
		try {
			if (opened()) {
				Component component = ctx.widgets.component(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_MAIN_CURRENT_CATEGORY);
				if (component.childrenCount() > 0) {
					return component.component(0).text().toLowerCase();
				} else {
					return ctx.widgets.component(WIDGET_INTERFACE_MAIN, 49).text().toLowerCase();
				}
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	public Component getCategoryRectangle(IClientContext ctx, String text) {
		for (Component component : ctx.widgets.component(WIDGET_INTERFACE_MAIN, 62).components()) {
			if (component.valid() && component.text().equalsIgnoreCase(text)) {
				return component;
			}
		}
		return null;
	}

	public boolean select(final String categoryString, final int itemId, final int quantity) {
		return select(categoryString, itemId) && setQuantity(quantity);
	}

	public boolean setQuantity(final int amount) {
		int targetQuantity;
		switch (amount) {
			case -1:
				if (ctx.backpack.count() == 28) {
					targetQuantity = -1;
				} else {
					targetQuantity = 28 - ctx.backpack.select().count();
				}
				break;
			case 0:
				targetQuantity = 28 - ctx.backpack.count();
				break;
			default:
				targetQuantity = amount;
		}

		if (targetQuantity <= -1) {
			return true;
		}

		int currentQuantity = getQuantity();
		if (currentQuantity == targetQuantity) {
			return true;
		}

		if (currentQuantity != -1) {
			final Timer t = new Timer(Math.abs(targetQuantity - currentQuantity) * Random.nextInt(2000, 3000));
			if (targetQuantity > currentQuantity) {
				while (t.running() && targetQuantity > currentQuantity) {
					if (!opened()) {
						break;
					}
					if (getQuantityPercent() >= 100) {
						return true;
					}
					if (increaseQuantity()) {
						ctx.sleep(200);
					}
					currentQuantity = getQuantity();
				}
			} else {
				while (t.running() && currentQuantity > targetQuantity) {
					if (!opened()) {
						break;
					}
					if (getQuantityPercent() <= 0) {
						return true;
					}
					if (decreaseQuantity()) {
						ctx.sleep(200);
					}
					currentQuantity = getQuantity();
				}
			}
		}

		return getQuantity() == targetQuantity;
	}

	private boolean decreaseQuantity() {
		final int currentQuantity = getQuantity();
		Component component = ctx.widgets.component(WIDGET_INTERFACE_MAIN, WIDGET_QUANTITY_DECREASE);
		if (component.valid() && component.interact("Decrease")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return currentQuantity != getQuantity();
				}
			}, 600, 5);
		}
		return currentQuantity != getQuantity();
	}

	public int getQuantity() {
		Component quantity = ctx.widgets.component(WIDGET_MAIN, WIDGET_MAIN_QUANTITY);
		if (quantity.valid()) {
			try {
				return Integer.parseInt(quantity.text());
			} catch (Exception ignored) {
			}
		}
		return -1;
	}

	public int getQuantityPercent() {
		Component component = ctx.widgets.component(WIDGET_INTERFACE_MAIN, 148);
		if (component.valid()) {
			final int x = component.relativePoint().x;
			if (x > 0) {
				return (int) (x * 100 / 151D);
			}
		}

		return -1;
	}

	private boolean increaseQuantity() {
		final int currentQuantity = getQuantity();
		Component component = ctx.widgets.component(WIDGET_INTERFACE_MAIN, WIDGET_QUANTITY_INCREASE);
		if (component.valid() && component.interact("Increase")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return currentQuantity != getQuantity();
				}
			}, 600, 5);
		}
		return currentQuantity != getQuantity();
	}

	public boolean select(final int categoryIndex, final int itemId, final int quantity) {
		return select(categoryIndex, itemId) && setQuantity(quantity);
	}

	public boolean start() {
		if (canStart()) {
			Component startButton = getStartButton();
			return startButton.valid() && startButton.interact(startButton.tooltip());
		}
		return false;
	}

	public boolean canStart() {
		final Component startButton = getStartButton();

		if (startButton.valid()) {
			final Pattern pattern = Pattern.compile("Make (\\d+) ");
			final Matcher matcher = pattern.matcher(startButton.tooltip());
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1)) > 0;
			}
		}

		return false;
	}

	private Component getStartButton() {
		return ctx.widgets.component(WIDGET_MAIN, WIDGET_MAIN_ACTION_TOOLTIP);
	}
}

