package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.util.TargetableRectangle;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Widget;

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
public class SkillingInterface extends org.powerbot.script.lang.ItemQuery<org.powerbot.script.wrappers.Item> {
	private static final int WIDGET_MAIN = 1370;
	private static final int WIDGET_MAIN_ACTION = 40;
	private static final int WIDGET_MAIN_ACTION_TOOLTIP = 37;
	private static final int WIDGET_MAIN_QUANTITY = 74;
	private static final int WIDGET_INTERFACE_MAIN = 1371;
	private static final int WIDGET_INTERFACE_CATEGORY_MENU = 60;
	private static final int WIDGET_INTERFACE_CATEGORY = 51;
	private static final int WIDGET_PICK_ITEM = 44;
	private static final int WIDGET_BUTTON_CLOSE = 30;
	private static final int WIDGET_QUANTITY_INCREASE = 29;
	private static final int WIDGET_QUANTITY_DECREASE = 34;
	private static final int WIDGET_SCROLLBAR_PARENT = 47;
	private static final int WIDGET_PRODUCTION_MAIN = 1251;
	private static final int WIDGET_PRODUCTION_PROGRESS = 33;
	private static final int WIDGET_PRODUCTION_CANCEL = 48;
	protected LogicailMethodContext ctx;

	public SkillingInterface(LogicailMethodContext context) {
		super(context);
		ctx = context;
	}

	public boolean isOpen() {
		return getMainWidget().getComponent(0).isValid();
	}

	public Widget getMainWidget() {
		return ctx.widgets.get(WIDGET_MAIN);
	}

	public Item getSelectedItem() {
		Component component = getMainWidget().getComponent(55);
		if (component.isValid()) {
			return new Item(ctx, component);
		}
		return getNil();
	}

	public String getSelectedName() {
		Component component = ctx.widgets.get(WIDGET_MAIN, 56);
		if (component.isValid()) {
			return component.getText();
		}

		return "";
	}

	public boolean select(final String category, final int itemId, final int quantity) {
		return select(category, itemId) && setQuantity(quantity);
	}

	public boolean select(final String category, final int itemId) {
		//System.out.println("Select " + category + " " + itemId);
		if (!isOpen()) {
			return false;
		}

		if (getSelectedItem().getId() == itemId) {
			return true;
		}

		setCategory(category);

		if (Random.nextBoolean() && Random.nextBoolean()) {
			if (select(new Filter<Item>() {
				@Override
				public boolean accept(Item item) {
					return item.isOnScreen();
				}
			}).count() > 1) {
				for (Item item : shuffle().first()) {
					item.interact("Select");
					sleep(250, 1000);
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

	private boolean setCategory(final String category) {
		final String currentCategory = getCategory();

		if (!currentCategory.equalsIgnoreCase(category)) {
			Component child = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_CATEGORY).getChild(0);
			if (child.isValid() && child.click(true)) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return getCategoryRectangle(category) != null;
					}
				})) {
					sleep(250, 750);
					TargetableRectangle rectangle = getCategoryRectangle(category);
					if (rectangle != null && ctx.mouse.move(rectangle)) {
						sleep(200, 500);
						if (rectangle.contains(ctx.mouse.getLocation()) && ctx.menu.click(Menu.filter("Select"))) {
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
		}

		return category.equalsIgnoreCase(getCategory());
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

	private Component getItemComponent() {
		return ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_PICK_ITEM);
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

	public String getAction() {
		try {
			if (isOpen()) {
				return getMainWidget().getComponent(WIDGET_MAIN_ACTION).getChild(0).getText();
			}
		} catch (Exception ignored) {
		}

		return "Unknown";
	}

	public boolean start() {
		if (canStart()) {
			Component widgetChild = getMainWidget().getComponent(WIDGET_MAIN_ACTION_TOOLTIP);
			return widgetChild.isValid() && widgetChild.interact(widgetChild.getTooltip());
		}
		return false;
	}

	public boolean canStart() {
		final Component component = getMainWidget().getComponent(WIDGET_MAIN_ACTION_TOOLTIP);

		if (component.isValid()) {
			final Pattern pattern = Pattern.compile("Make (\\d+) ");
			final Matcher matcher = pattern.matcher(component.getTooltip());
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1)) > 0;
			}
		}

		return false;
	}

	private Widget getProductionWidget() {
		return ctx.widgets.get(WIDGET_PRODUCTION_MAIN);
	}

	public boolean isProductionInterfaceOpen() {
		Component component = getProductionWidget().getComponent(5);
		return component.isValid() && component.isVisible();
	}

	public boolean isProductionComplete() {
		final Component component = getProductionWidget().getComponent(WIDGET_PRODUCTION_PROGRESS);

		if (component.isValid() && component.isVisible()) {
			final String[] split = component.getText().split("/");
			return split.length == 2 && split[0].equals(split[1]);
		}

		return false;
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

			if (getProductionWidget().getComponent(WIDGET_PRODUCTION_CANCEL).interact("Cancel") && Condition.wait(new Callable<Boolean>() {
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
			Component component = getMainWidget().getComponent(WIDGET_BUTTON_CLOSE);
			if (component.isValid() && component.interact("Close")) {
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

	public int getQuantity() {
		Component component = ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_QUANTITY);
		if (component.isValid()) {
			try {
				return Integer.parseInt(component.getText());
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
					//System.out.println("Q%: " + getQuantityPercent());
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

	@Override
	public Item getNil() {
		return ctx.backpack.getNil();
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new LinkedList<Item>();

		final Component[] children = getItemComponent().getChildren();
		if (children != null && children.length > 0) {
			for (Component child : children) {
				if (child.getItemId() != -1) {
					items.add(new Item(ctx, child));
				}
			}
		}

		return items;
	}
}

