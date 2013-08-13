package org.logicail.api.methods;

import org.logicail.framework.script.LoopTask;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/07/13
 * Time: 10:37
 */
public abstract class SimplePaint extends LoopTask implements MouseListener, MouseMotionListener/*, MouseWheelListener*/ {
	private static final int MIN_PADDING = 8;
	public final Map<String, String> contents = Collections.synchronizedMap(new LinkedHashMap<String, String>());
	private static final int PAINT_LINE_SPACE = 18;
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

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	/*@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (rectangle.contains(e.getPoint())) {
			alpha = Math.max(32, Math.min(alpha + e.getWheelRotation() * 12, 255));
			background = new Color(0, 0, 0, alpha);
		}
	}*/

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

			Dimension dimensions = ctx.game.getDimensions();

			int xMax = dimensions.width - MIN_PADDING - rectangle.width;
			int yMax = dimensions.height - MIN_PADDING - rectangle.height;

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


	public BufferedImage getImage() {
		Dimension dimensions = ctx.game.getDimensions();
		BufferedImage image = new BufferedImage(dimensions.width, dimensions.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();

		draw(g2);

		return trimImage(image);
	}

	/**
	 * Remove transparent pixels
	 *
	 * @return
	 */
	public BufferedImage trimImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		int xMin = 0;
		int xMax = width;

		int yMin = 0;
		int yMax = height;

		try {
			for (int y = 0; y < height - 1; ++y) {
				if (!isTransparentRow(image, y)) {
					yMin = y;
					break;
				}
			}

			for (int y = height - 1; y > 0; --y) {
				if (isTransparentRow(image, y)) {
					yMax = y + 1;
					break;
				}
			}

			for (int x = 0; x < width - 1; ++x) {
				if (!isTransparentColumn(image, x)) {
					xMin = x;
					break;
				}
			}

			for (int x = xMax - 1; x > 0; --x) {
				if (!isTransparentColumn(image, x)) {
					xMax = x + 1;
					break;
				}
			}

			return image.getSubimage(xMin, yMin, xMax - xMin, yMax - yMin);
		} catch (Exception e) {
			// encase I made a mistake
			return image;
		}
	}

	private boolean isTransparentRow(BufferedImage image, int y) {
		int width = image.getWidth();
		for (int i = 0; i < width - 1; i++) {
			if (image.getRGB(i, y) != 0) {
				return false;
			}
		}

		return true;
	}

	private boolean isTransparentColumn(BufferedImage image, int x) {
		int height = image.getHeight();
		for (int i = 0; i < height - 1; i++) {
			if (image.getRGB(x, i) != 0) {
				return false;
			}
		}

		return true;
	}
}
