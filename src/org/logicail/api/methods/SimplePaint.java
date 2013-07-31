package org.logicail.api.methods;

import org.logicail.framework.script.LoopTask;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/07/13
 * Time: 10:37
 */
public abstract class SimplePaint extends LoopTask implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final int MIN_PADDING = 8;
	public final Map<String, String> contents = Collections.synchronizedMap(new LinkedHashMap<String, String>());
	private final int PAINT_LINE_SPACE = 18;
	private final Rectangle rectangle;
	private Point start;
	private int x = MIN_PADDING;
	private int y = MIN_PADDING;
	private int height = 1;
	private int width = 1;
	private Font fontTitle = new Font("Dialog", Font.BOLD, 16);
	private Font fontNormal = new Font("Dialog", Font.PLAIN, 12);
	private int alpha = 192;
	private Color border = new Color(255, 255, 255, alpha);
	private Color background = new Color(0, 0, 0, alpha);
	private boolean moving;

	public SimplePaint(LogicailMethodContext ctx) {
		super(ctx);

		rectangle = new Rectangle(x, y, 1, 1);

		contents.put("NAME", ctx.script.getName());
		contents.put("VERSION", String.valueOf(ctx.script.getVersion()));
		contents.put("LINE_1", "");
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (rectangle.contains(e.getPoint())) {
			alpha = Math.max(32, Math.min(alpha - e.getWheelRotation() * 12, 255));
			background = new Color(0, 0, 0, alpha);
		}
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		height = contents.size() * PAINT_LINE_SPACE + 16;

		g.setColor(background);
		g2d.fill(rectangle);
		g.setColor(border);
		g2d.draw(rectangle);

		int widthCopy = width;
		width = 1;

		int y = this.y + PAINT_LINE_SPACE + 8;

		synchronized (contents) {
			for (Map.Entry<String, String> next : contents.entrySet()) {
				String value = next.getValue();
				String key = next.getKey();
				if (key.equals("NAME") || key.equals("VERSION")) {
					g.setFont(fontTitle);
					int w = g.getFontMetrics().stringWidth(value);
					if (w > width) {
						width = w;
					}
					int x = this.x + (widthCopy - w) / 2;
					g.drawString(value, x, y);
				} else if (key.startsWith("LINE_")) {
					g.drawLine(x + MIN_PADDING, y - PAINT_LINE_SPACE / 2, x + widthCopy - MIN_PADDING, y - PAINT_LINE_SPACE / 2);
					y -= PAINT_LINE_SPACE / 3;
				} else if (!value.isEmpty()) {
					g.setFont(fontNormal);
					value = key + ": " + value;
					int w = g.getFontMetrics().stringWidth(value);
					if (w > width) {
						width = w;
					}
					g.drawString(value, x + MIN_PADDING, y);
				}
				y += PAINT_LINE_SPACE;
			}
		}

		width += MIN_PADDING + MIN_PADDING;

		rectangle.setFrame(x, this.y, width, height);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && rectangle.contains(e.getPoint())) {
			moving = true;
			start = e.getPoint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		moving = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (moving) {
			Component component = e.getComponent();

			Point point = e.getPoint();

			if (!component.contains(point)) {
				return;
			}

			int xMax = component.getWidth() - MIN_PADDING - rectangle.width;
			int yMax = component.getHeight() - MIN_PADDING - rectangle.height;

			x += point.x - start.x;
			y += point.y - start.y;
			start = point;

			if (x < MIN_PADDING) {
				x = MIN_PADDING;
			} else if (x > xMax) {
				x = xMax;
			}

			if (y < MIN_PADDING) {
				y = MIN_PADDING;
			} else if (y > yMax) {
				y = yMax;
			}

			rectangle.setFrame(x, y, rectangle.width, rectangle.height);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
