package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.util.TargetableRectangle;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.awt.*;
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
public class ISkillingInterface extends org.powerbot.script.lang.ItemQuery<org.powerbot.script.wrappers.Item> {
	private static final int WIDGET_MAIN = 1370;
	private static final int WIDGET_MAIN_ACTION = 40;
	private static final int WIDGET_MAIN_ACTION_TOOLTIP = 37;
	private static final int WIDGET_MAIN_QUANTITY = 74;
	private static final int WIDGET_INTERFACE_MAIN = 1371;
	private static final int WIDGET_INTERFACE_MAIN_ITEMS = 44;
	private static final int WIDGET_INTERFACE_MAIN_SELECTED_ITEM = 55;
	private static final int WIDGET_INTERFACE_CATEGORY_MENU = 60;
	private static final int WIDGET_INTERFACE_CATEGORY = 51;
	private static final int WIDGET_MAIN_CLOSE_BUTTON = 30;
	private static final int WIDGET_QUANTITY_INCREASE = 29;
	private static final int WIDGET_QUANTITY_DECREASE = 34;
	private static final int WIDGET_SCROLLBAR_PARENT = 47;
	private static final int WIDGET_PRODUCTION_MAIN = 1251;
	private static final int WIDGET_PRODUCTION_PROGRESS = 33;
	private static final int WIDGET_PRODUCTION_CANCEL = 48;
	protected IMethodContext ctx;


	public ISkillingInterface(IMethodContext context) {
		super(context);
		ctx = context;
	}

	public boolean cancelProduction() {
		if (isProductionInterfaceOpen()) {
			if (isProductionComplete()) {
				ctx.log.info("Production Complete");
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !isProductionInterfaceOpen();
					}
				})) {
					return true;
				}
			}

			if (ctx.widgets.get(WIDGET_PRODUCTION_MAIN, WIDGET_PRODUCTION_CANCEL).interact("Cancel") && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !isProductionInterfaceOpen();
				}
			})) {
				sleep(100, 600);
			}
		}

		return !isProductionInterfaceOpen();
	}

	public boolean isProductionComplete() {
		final Component component = ctx.widgets.get(WIDGET_PRODUCTION_MAIN, WIDGET_PRODUCTION_PROGRESS);

		if (component.isValid() && component.isVisible()) {
			final String[] split = component.getText().split("/");
			return split.length == 2 && split[0].equals(split[1]);
		}

		return false;
	}

	public boolean isProductionInterfaceOpen() {
		Component component = ctx.widgets.get(WIDGET_PRODUCTION_MAIN, 5);
		return component.isValid() && component.isVisible();
	}

	public boolean close() {
		if (!isOpen()) {
			return true;
		}

		/*if (Random.nextBoolean()) {
			ctx.keyboard.send("{VK_ESCAPE down}");
			ctx.keyboard.send("{VK_ESCAPE up}");
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !isOpen();
				}
			});
		} else*/
		{
			Component closeButton = ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_CLOSE_BUTTON);
			if (closeButton.isValid() && closeButton.interact("Close")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !isOpen();
					}
				});
			}
		}

		return !isOpen();
	}

	public boolean isOpen() {
		return ctx.widgets.get(WIDGET_MAIN, 0).isValid();
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new LinkedList<Item>();

		final Component[] children = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_MAIN_ITEMS).getChildren();
		if (children != null && children.length > 0) {
			for (Component child : children) {
				if (child.getItemId() != -1) {
					items.add(new Item(ctx, child));
				}
			}
		}

		return items;
	}

	public String getAction() {
		try {
			if (isOpen()) {
				return ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_ACTION).getChild(0).getText();
			}
		} catch (Exception ignored) {
		}

		return "Unknown";
	}

	public Item getSelectedItem() {
		Component component = ctx.widgets.get(WIDGET_MAIN, WIDGET_INTERFACE_MAIN_SELECTED_ITEM);
		if (component.isValid()) {
			return new Item(ctx, component);
		}
		return getNil();
	}

	@Override
	public Item getNil() {
		return ctx.backpack.getNil();
	}

	public boolean select(final String categoryString, final int itemId) {
		return select(getCategoryIndexOf(new Filter<String>() {
			@Override
			public boolean accept(String s) {
				return s.equalsIgnoreCase(categoryString);
			}
		}), itemId);
	}

	public int getCategoryIndexOf(org.powerbot.script.lang.Filter<String> filter) {
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

		final Component[] options = ctx.widgets.get(WIDGET_INTERFACE_MAIN, 62).getChildren();

		for (Component option : options) {
			final String text = option.getText();
			if (isOpen() && !text.isEmpty()) {
				list.add(text);
			}
		}

		return list.toArray(new String[list.size()]);
	}

	public boolean select(final int categoryIndex, final int itemId) {
		//ctx.log.info(String.format("ISkillingInterface %d %d", categoryIndex, itemId));
		if (!isOpen()) {
			return false;
		}

		if (getSelectedItem().getId() == itemId) {
			return true;
		}

		setCategory(categoryIndex);

		if (Random.nextBoolean() && Random.nextBoolean()) {
			if (!select().select(new Filter<Item>() {
				@Override
				public boolean accept(Item item) {
					return item.isOnScreen();
				}
			}).isEmpty()) {
				for (Item item : shuffle().first()) {
					if (item.interact("Select")) {
						sleep(250, 1000);
					}
				}
			}
		}

		for (Item item : select().id(itemId).first()) {
			if (item.isOnScreen() || ctx.widgets.scroll(item.getComponent(), ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_SCROLLBAR_PARENT), true)) {
				if (item.interact("Select")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return getSelectedItem().getId() == itemId;
						}
					});
				}
			}
		}

		return getSelectedItem().getId() == itemId;
	}

	private boolean setCategory(int index) {
		final String currentCategory = getCategory();
		final String[] categorys = getCategorys();

		if (categorys.length > index) {
			final String target = categorys[index];
			if (currentCategory.equalsIgnoreCase(target)) {
				return true;
			}

			Component child = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_CATEGORY).getChild(0);
			if (child.isValid() && child.click(true)) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return getCategoryRectangle(target) != null;
					}
				})) {
					sleep(250, 750);
					TargetableRectangle rectangle = getCategoryRectangle(target);
					if (rectangle != null && ctx.mouse.move(rectangle)) {
						sleep(200, 500);
						if (rectangle.contains(ctx.mouse.getLocation()) && ctx.menu.click(org.powerbot.script.methods.Menu.filter("Select"))) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return !currentCategory.equalsIgnoreCase(getCategory());
								}
							});
						}
						sleep(200, 600);
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
			if (isOpen()) {
				Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, 51);
				if (component.getChildren().length > 0) {
					return component.getChild(0).getText().toLowerCase();
				} else {
					return ctx.widgets.get(WIDGET_INTERFACE_MAIN, 49).getText().toLowerCase();
				}
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	public TargetableRectangle getCategoryRectangle(String text) {
		final Component widgetChild = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_CATEGORY_MENU);
		final Component[] options = ctx.widgets.get(WIDGET_INTERFACE_MAIN, 62).getChildren();

		for (int i = 0; i < options.length; i++) {
			if (options[i].isValid() && options[i].getText().equalsIgnoreCase(text)) {
				Rectangle rectangle = widgetChild.getBoundingRect();
				rectangle.x += 4;
				rectangle.y += i * 15 + 7;
				rectangle.width -= 9;
				rectangle.height = 10;
				return new TargetableRectangle(rectangle);
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
					targetQuantity = 27 - ctx.backpack.count();
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
				while (t.isRunning() && !ctx.isPaused() && !ctx.isShutdown() && targetQuantity > currentQuantity) {
					if (!isOpen()) {
						break;
					}
					if (getQuantityPercent() >= 100) {
						return true;
					}
					if (increaseQuantity()) {
						sleep(100, 400);
					}
					currentQuantity = getQuantity();
				}
			} else {
				while (t.isRunning() && !ctx.isPaused() && !ctx.isShutdown() && currentQuantity > targetQuantity) {
					if (!isOpen()) {
						break;
					}
					if (getQuantityPercent() <= 0) {
						return true;
					}
					if (decreaseQuantity()) {
						sleep(100, 400);
					}
					currentQuantity = getQuantity();
				}
			}
		}

		return getQuantity() == targetQuantity;
	}

	private boolean decreaseQuantity() {
		final int currentQuantity = getQuantity();
		Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_QUANTITY_DECREASE);
		if (component.isValid() && component.interact("Decrease")) {
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
		Component quantity = ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_QUANTITY);
		if (quantity.isValid()) {
			try {
				return Integer.parseInt(quantity.getText());
			} catch (Exception ignored) {
			}
		}
		return -1;
	}

	public int getQuantityPercent() {
		Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, 148);
		if (component.isValid()) {
			final int x = component.getRelativeLocation().x;
			if (x > 0) {
				return (int) (x * 100 / 151D);
			}
		}

		return -1;
	}

	private boolean increaseQuantity() {
		final int currentQuantity = getQuantity();
		Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_QUANTITY_INCREASE);
		if (component.isValid() && component.interact("Increase")) {
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
			return startButton.isValid() && startButton.interact(startButton.getTooltip());
		}
		return false;
	}

	public boolean canStart() {
		final Component startButton = getStartButton();

		if (startButton.isValid()) {
			final Pattern pattern = Pattern.compile("Make (\\d+) ");
			final Matcher matcher = pattern.matcher(startButton.getTooltip());
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1)) > 0;
			}
		}

		return false;
	}

	private Component getStartButton() {
		return ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_ACTION_TOOLTIP);
	}
}

