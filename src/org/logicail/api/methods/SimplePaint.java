package org.logicail.api.methods;

import org.powerbot.script.lang.Drawable;

import java.awt.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/07/13
 * Time: 10:37
 */
public class SimplePaint implements Drawable {

	private static final int PAINT_LINE_SPACE = 18;
	private static final int PAINT_X = 8;
	private static final int PAINT_Y = 32;
	public final Map<String, String> contents = Collections.synchronizedMap(new LinkedHashMap<String, String>());
	private final Rectangle rectangle = new Rectangle(PAINT_X, PAINT_Y, 1, 1);
	private int height = 1;
	private int width = 1;
	private Font fontTitle = new Font("Dialog", Font.BOLD, 16);
	private Font fontNormal = new Font("Dialog", Font.BOLD, 12);
	private Color border = new Color(255, 255, 255, 196);
	private Color background = new Color(0, 0, 0, 196);

	@Override
	public void draw(Graphics g, int i) {
		draw(g, 255);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		height = contents.size() * PAINT_LINE_SPACE + 16;

		g.setColor(background);
		g2d.fill(rectangle);
		g.setColor(border);
		g2d.draw(rectangle);

		int widthCopy = width;
		width = 1;

		int y = PAINT_Y + PAINT_LINE_SPACE + 8;

		synchronized (contents) {
			Iterator<Map.Entry<String, String>> iterator = contents.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> next = iterator.next();
				String value = next.getValue();
				String key = next.getKey();
				if (key.equals("NAME") || key.equals("VERSION")) {
					g.setFont(fontTitle);
					int w = g.getFontMetrics().stringWidth(value);
					if (w > width) {
						width = w;
					}
					int x = (PAINT_X + 8 + widthCopy - w) / 2;
					g.drawString(value, x, y);
				} else if (key.startsWith("LINE_")) {
					g.drawLine(PAINT_X + 8, y - PAINT_LINE_SPACE / 2, widthCopy, y - PAINT_LINE_SPACE / 2);
					y -= PAINT_LINE_SPACE / 3;
				} else if (!value.isEmpty()) {
					g.setFont(fontNormal);
					value = key + ": " + value;
					int w = g.getFontMetrics().stringWidth(value);
					if (w > width) {
						width = w;
					}
					g.drawString(value, PAINT_X + 8, y);
				}
				y += PAINT_LINE_SPACE;
			}
		}

		width += PAINT_X + 8;

		rectangle.setFrame(PAINT_X, PAINT_Y, width, height);
	}
}
