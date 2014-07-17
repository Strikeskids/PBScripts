package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.PaintListener;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class Painter extends IClientAccessor implements PaintListener {
	// Public encase want to load custom font
	public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 14);
	public static final Font FONT_SMALL = new Font("Arial", Font.BOLD, 12);
	private static final Color COLOUR_BACKGROUND = new Color(0, 0, 0, 128);
	private static final Color COLOUR_BORDER = new Color(255, 255, 255, 192);
	private final BasicStroke mouseStroke = new BasicStroke(2f);
	private final String title;
	private Point location = new Point(50, 220);
	private final Rectangle backgroundRectangle = new Rectangle(location.x, location.y, 1, 1);
	private LinkedHashMap<Object, Object> properties;

	public void location(Point point) {
		location = point;
	}

	public Painter(IClientContext ctx, String name, String version) {
		super(ctx);
		if (version != null) {
			title = String.format("%s v%s", name, version);
		} else {
			title = name;
		}
	}

	public Painter properties(LinkedHashMap<Object, Object> properties) {
		this.properties = properties;
		return this;
	}

	public void repaint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		ctx.log.repaint(g, 50, 50);

		int x = location.x + 10;
		int y = location.y + 20;
		int width = 1;

		g.setColor(COLOUR_BACKGROUND);
		g2d.fill(backgroundRectangle);
		g.setColor(COLOUR_BORDER);
		g2d.draw(backgroundRectangle);

		g.setColor(Color.WHITE);
		g.setFont(FONT_TITLE);
		FontMetrics fontMetrics = g.getFontMetrics();
		width = Math.max(width, fontMetrics.stringWidth(title));
		//g.drawString(title, x, y);

		Rectangle2D r = fontMetrics.getStringBounds(title, g2d);
		int centerX = location.x + (backgroundRectangle.width - (int) r.getWidth()) / 2;
		g.drawString(title, centerX, y);

		y += 10;
		g.drawLine(x - 5, y, x + backgroundRectangle.width - 15, y);
		y += 20;

		// Properties
		if (properties != null) {
			g.setFont(FONT_SMALL);
			g.setColor(Color.WHITE);
			fontMetrics = g.getFontMetrics();
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				final String string = String.format("%s: %s", entry.getKey(), entry.getValue());
				width = Math.max(width, fontMetrics.stringWidth(string));
				g.drawString(string, x, y);
				y += 15;
			}
		}

		drawMouse(g2d);

		// Resize background
		backgroundRectangle.x = location.x;
		backgroundRectangle.y = location.y;
		backgroundRectangle.width = width + 20;
		backgroundRectangle.height = y - location.y - 5;
	}

	private void drawMouse(Graphics2D g2d) {
		g2d.setStroke(mouseStroke);
		final Point mouse = ctx.input.getLocation();
		g2d.drawLine(mouse.x - 5, mouse.y - 5, mouse.x + 5, mouse.y + 5);
		g2d.drawLine(mouse.x - 5, mouse.y + 5, mouse.x + 5, mouse.y - 5);
	}
}
