package org.logicail.rsbot.scripts.rt6.lootdisplayer;

import com.logicail.loader.rt6.wrapper.ItemDefinition;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.*;
import org.powerbot.script.rt6.GeItem;
import org.powerbot.script.rt6.GroundItem;
import org.powerbot.script.rt6.Interactive;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 15:58
 */
@Script.Manifest(name = "Loot displayer", description = "Displays loot value", properties = "hidden=true;")
public class LootDisplayer extends PollingScript<IClientContext> implements PaintListener, KeyListener {
	private static final int COIN_ID = 995;
	private Map<Integer, Integer> prices = new HashMap<Integer, Integer>() {{
		put(COIN_ID, 1);
	}};

	private final Font font = new Font("SansSerif", Font.BOLD, 10);
	private final DecimalFormat format = new DecimalFormat("###,###,###.#");
	private final AtomicBoolean show = new AtomicBoolean();

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ALT) {
			show.set(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ALT) {
			show.set(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void poll() {
		for (GroundItem item : ctx.groundItems.select().select(Interactive.areInViewport())) {
			if (!prices.containsKey(item.id())) {
				ItemDefinition definition = ctx.definitions.get(item).unnoted();
				if (definition.tradable) {
					final int price = GeItem.price(definition.id);
					prices.put(definition.id, price);
					if (definition.noteId > -1) {
						prices.put(definition.noteId, price);
					}
				}
			}
		}

		Condition.sleep(333);
	}

	@Override
	public void repaint(Graphics g) {
		if (!show.get()) {
			return;
		}

		g.setFont(font);
		final FontMetrics fm = g.getFontMetrics();

		// TODO: move this out of repaint
		HashMap<Tile, HashMap<Integer, Integer>> map = new HashMap<Tile, HashMap<Integer, Integer>>();
		for (final GroundItem item : ctx.groundItems.select().within(25).select(new Filter<GroundItem>() {
			@Override
			public boolean accept(GroundItem groundItem) {
				return prices.containsKey(groundItem.id()) && groundItem.inViewport();
			}
		})) {
			if (!map.containsKey(item.tile())) {
				map.put(item.tile(), new HashMap<Integer, Integer>() {{
					put(item.id(), item.stackSize());
				}});
			} else {
				HashMap<Integer, Integer> count = map.get(item.tile());
				if (count.containsKey(item.id())) {
					count.put(item.id(), count.get(item.id()) + item.stackSize());
				} else {
					count.put(item.id(), item.stackSize());
				}
			}
		}

		for (Map.Entry<Tile, HashMap<Integer, Integer>> entry : map.entrySet()) {
			ArrayList<DisplayItem> list = new ArrayList<DisplayItem>();
			for (Map.Entry<Integer, Integer> item : entry.getValue().entrySet()) {
				ItemDefinition definition = ctx.definitions.item(item.getKey());
				if (definition.id == COIN_ID) {
					list.add(new DisplayItem(definition.name, prices.get(item.getKey()) * item.getValue(), 1));
				} else {
					list.add(new DisplayItem(definition.name, prices.get(item.getKey()), item.getValue()));
				}
			}

			Collections.sort(list, new Comparator<DisplayItem>() {
				@Override
				public int compare(DisplayItem o1, DisplayItem o2) {
					return Integer.valueOf(o1.price * o1.stacksize).compareTo(o2.price * o2.stacksize);
				}
			});

			Point p = entry.getKey().matrix(ctx).centerPoint();
			p.y -= list.size() * 8;

			for (DisplayItem item : list) {
				String str = item.toString();
				final int width = fm.stringWidth(str);
				int x = p.x - width / 2;
				g.setColor(Color.BLACK);
				g.drawString(str, x + 1, p.y + 1);
				g.drawString(str, x + 1, p.y - 1);
				g.drawString(str, x - 1, p.y - 1);
				g.drawString(str, x - 1, p.y + 1);
				g.setColor(Color.WHITE);
				g.drawString(str, x, p.y);
				p.y += 8;
			}
		}
	}

	class DisplayItem {
		final String name;
		final int price;
		final int stacksize;

		public DisplayItem(String name, int price, int stacksize) {
			this.name = name;
			this.price = price;
			this.stacksize = stacksize;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append(name);

			if (stacksize > 1) {
				sb.append(" [").append(stacksize).append("]");
			}

			if (price > 0) {
				sb.append(" ");
				int total = price * stacksize;
				if (total > 1000000) {
					sb.append(format.format(total / 1000000f)).append("m");
				} else if (total > 1000) {
					sb.append(format.format(total / 1000f)).append("k");
				} else {
					sb.append(format.format(total));
				}
			}

			return sb.toString();
		}
	}
}
