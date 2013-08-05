package org.logicail.api.methods.providers;

import org.logicail.api.methods.LogicailMethodContext;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Widget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:29
 */
public class SkillingInterface extends ItemQuery<Item> {
	private static final int WIDGET_MAIN = 1370;
	private static final int WIDGET_MAIN_ACTION = 40;
	private static final int WIDGET_MAIN_ACTION_TOOLTIP = 37;
	private static final int WIDGET_MAIN_QUANTITY = 73;
	private static final int WIDGET_INTERFACE_MAIN = 1371;
	private static final int WIDGET_INTERFACE_CATEGORY_MENU = 60;
	private static final int WIDGET_INTERFACE_CATEGORY = 51;
	private static final int WIDGET_PICK_ITEM = 44;
	private static final int WIDGET_BUTTON_CLOSE = 30;
	private static final int WIDGET_QUANTITY_INCREASE = 29;
	private static final int WIDGET_QUANTITY_DECREASE = 34;
	private static final int WIDGET_SCROLLBAR_PARENT = 47;
	private static final int WIDGET_PRODUCTION_MAIN = 1251;
	private static final int WIDGET_PRODUCTION_CANCEL = 48;
	private static final int WIDGET_MAIN_SELECTED_ITEM = 55;
	private static final int WIDGET_MAIN_SELECTED_ITEM_NAME = 56;
	public LogicailMethodContext ctx;

	public SkillingInterface(LogicailMethodContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}

	public boolean isOpen() {
		return getMainWidget().isValid();
	}

	public Widget getMainWidget() {
		return ctx.widgets.get(WIDGET_MAIN);
	}

	public Item getSelectedItem() {
		if (!isOpen()) {
			return null;
		}

		Component component = ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_SELECTED_ITEM);
		if (component != null && component.isValid()) {
			return new Item(ctx, component);
		}

		return getNil();
	}

	public String getSelectedName() {
		if (isOpen()) {
			Component component = ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_SELECTED_ITEM_NAME);
			if (component != null && component.isValid()) {
				String text = component.getText();
				if (text != null) {
					return text;
				}
			}
		}
		return getNil().getName();
	}

	public boolean select(final String category, final int itemId, final int quantity) {
		return select(category, itemId) && setQuantity(quantity);
	}

	public boolean select(final String category, final int itemId) {
		if (!isOpen()) {
			return false;
		}

		Item selectedItem = getSelectedItem();
		if (selectedItem != null && selectedItem.getId() == itemId) {
			return true;
		}

		final String currentCategory = getCategory();

		if (!currentCategory.equalsIgnoreCase(category)) {
			Component child = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_CATEGORY).getChild(0);

			if (select().id(itemId).isEmpty() && child.isValid() && child.click(true)) {
				if (ctx.waiting.wait(3000, new Condition() {
					@Override
					public boolean validate() {
						return getCategoryRectangle(category) != null;
					}
				})) {
					sleep(50, 500);
					Rectangle rectangle = getCategoryRectangle(category);
					if (rectangle != null) {
						Timer t = new Timer(Random.nextInt(4000, 5000));
						while (t.isRunning()) {
							if (ctx.mouse.move(rectangle)) {
								if (ctx.menu.indexOf("Select") > -1 && ctx.menu.click("Select")) {
									ctx.waiting.wait(3000, new Condition() {
										@Override
										public boolean validate() {
											return !currentCategory.equalsIgnoreCase(getCategory());
										}
									});
									break;
								}
							}
							if ((rectangle = getCategoryRectangle(category)) == null) {
								break;
							}
						}
						sleep(100, 200);
					}
				}
			}
		}

		for (Item item : select().id(itemId).first()) {
			if (item.isValid()) {
				// TODO: Check what boolean is for
				ctx.widgets.scroll(item.getComponent(), ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_SCROLLBAR_PARENT), true);
				if (item.isOnScreen() && item.click()) { // TODO: Check interaction
					ctx.waiting.wait(1500, new Condition() {
						@Override
						public boolean validate() {
							Item selectedItem1 = getSelectedItem();
							return selectedItem1 == null || selectedItem1.getId() == itemId;
						}
					});
				}
			}
		}

		selectedItem = getSelectedItem();
		return selectedItem != null && selectedItem.getId() == itemId;
	}

	public String getCategory() {
		try {
			if (isOpen()) {
				Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, 51);
				if (component.getChildrenCount() > 0) {
					return component.getChild(0).getText().toLowerCase();
				} else {
					return ctx.widgets.get(WIDGET_INTERFACE_MAIN, 49).getText().toLowerCase();
				}
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	private Component getItemWidget() {
		return ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_PICK_ITEM);
	}

	public Rectangle getCategoryRectangle(String text) {
		final Component widgetChild = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_INTERFACE_CATEGORY_MENU);
		final Component[] options = ctx.widgets.get(WIDGET_INTERFACE_MAIN, 62).getChildren();

		for (int i = 0; i < options.length; i++) {
			if (options[i].isValid() && options[i].getText().equalsIgnoreCase(text)) {
				Rectangle rectangle = widgetChild.getBoundingRect();
				rectangle.x += 4;
				rectangle.y += i * 15 + 7;
				rectangle.width -= 9;
				rectangle.height = 10;
				return rectangle;
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
			Component component = ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_ACTION_TOOLTIP);
			if (component.isValid() && component.interact(component.getTooltip())) {
				ctx.waiting.wait(3000, new Condition() {
					@Override
					public boolean validate() {
						return !isOpen();
					}
				});
				return true;
			}
		}
		return false;
	}

	public boolean canStart() {
		final Component component = getMainWidget().getComponent(WIDGET_MAIN_ACTION_TOOLTIP);

		// TODO: Change to texture checking?

		if (component != null && component.isValid()) {
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
		Component component = ctx.widgets.get(WIDGET_PRODUCTION_MAIN, 5);
		return component != null && component.isValid() && component.isVisible();
	}

	public boolean cancelProduction() {
		if (isProductionInterfaceOpen()) {
			Component component = getProductionWidget().getComponent(WIDGET_PRODUCTION_CANCEL);
			if (component != null && component.isValid() && component.interact("Cancel")) {
				if (ctx.waiting.wait(1000, new Condition() {
					@Override
					public boolean validate() {
						return !isProductionInterfaceOpen();
					}
				})) {
					sleep(50, 500);
				}
			}
		}

		return !isProductionInterfaceOpen();
	}

	public boolean close() {
		if (!isOpen()) {
			return true;
		}

		Component component = getMainWidget().getComponent(WIDGET_BUTTON_CLOSE);
		if (component != null && component.isValid() && component.interact("Close")) {
			ctx.waiting.wait(2000, new Condition() {
				@Override
				public boolean validate() {
					return !isOpen();
				}
			});
		}

		return isOpen();
	}

	public int getQuantity() {
		final Component widgetChild = ctx.widgets.get(WIDGET_MAIN, WIDGET_MAIN_QUANTITY);
		if (widgetChild != null && widgetChild.isValid()) {
			return Integer.parseInt(widgetChild.getText());
		}
		return -1;
	}

	public int getQuantityPercent() {
		final Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, 148);
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
				if (ctx.backpack.isFull()) {
					targetQuantity = -1;
				} else {
					targetQuantity = 27 - ctx.backpack.select().count();
				}
				break;
			case 0:
				targetQuantity = 28 - ctx.backpack.select().count();
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
			//final ScriptHandler scriptHandler = Bot.context().getScriptHandler();
			final Timer t = new Timer(Math.abs(targetQuantity - currentQuantity) * Random.nextInt(2000, 3000));
			if (targetQuantity > currentQuantity) {
				while (t.isRunning() && !ctx.script.getController().isStopping() && targetQuantity > currentQuantity) {
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
				while (t.isRunning() && !ctx.script.getController().isStopping() && currentQuantity > targetQuantity) {
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
		final Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_QUANTITY_DECREASE);
		if (component != null && component.isValid() && component.interact("Decrease")) {
			ctx.waiting.wait(1000, new Condition() {
				@Override
				public boolean validate() {
					return currentQuantity != getQuantity();
				}
			});
		}
		return currentQuantity != getQuantity();
	}

	private boolean increaseQuantity() {
		final int currentQuantity = getQuantity();
		final Component component = ctx.widgets.get(WIDGET_INTERFACE_MAIN, WIDGET_QUANTITY_INCREASE);
		if (component != null && component.isValid() && component.interact("Increase")) {
			ctx.waiting.wait(1000, new Condition() {
				@Override
				public boolean validate() {
					return currentQuantity != getQuantity();
				}
			});
		}
		return currentQuantity != getQuantity();
	}

	@Override
	protected List<Item> get() {
		final java.util.List<Item> items = new ArrayList<>();
		final Component component = getItemWidget();
		if (component != null) {
			final Component[] children = component.getChildren();
			for (Component child : children) {
				if (child.getId() != -1) {
					items.add(new Item(ctx, child));
				}
			}
		}
		return items;
	}

	@Override
	public Item getNil() {
		return ctx.backpack.getNil();
	}
}
