package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.powerbot.event.PaintListener;
import org.powerbot.script.AbstractScript;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;

public class Painter extends LogicailMethodProvider implements PaintListener {
	// Public encase want to load custom font
	public static Font FONT_TITLE = new Font("Arial", Font.BOLD, 14);
	public static Font FONT_SMALL = new Font("Arial", Font.BOLD, 12);

	private static final Color COLOUR_BACKGROUND = new Color(0, 0, 0, 128);
	private static final Color COLOUR_BORDER = new Color(255, 255, 255, 192);
	private LinkedProperties properties;
	private final BasicStroke mouseStroke = new BasicStroke(2f);
	private final String title;

	private Point location = new Point(50, 200);
	private Rectangle backgroundRectangle = new Rectangle(location.x, location.y, 1, 1);

	public Painter(LogicailMethodContext context, AbstractScript script) {
		super(context);
		title = String.format("%s v%s", script.getName(), script.getVersion());
	}

	public Painter properties(LinkedProperties properties) {
		this.properties = properties;
		return this;
	}

	public void repaint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

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
		y += 15;

		// Properties
		if (properties != null) {
			g.setFont(FONT_SMALL);
			g.setColor(Color.WHITE);
			final Enumeration<Object> keys = properties.keys();
			fontMetrics = g.getFontMetrics();
			while (keys.hasMoreElements()) {
				final Object element = keys.nextElement();
				final String string = String.format("%s: %s", element, properties.get(element));
				width = Math.max(width, fontMetrics.stringWidth(string));
				g.drawString(string, x, y);
				y += 15;
			}
		}

		drawMouse(g2d);

		// Resize background
		backgroundRectangle.width = width + 20;
		backgroundRectangle.height = y - location.y;
	}

	private void drawMouse(Graphics2D g2d) {
		g2d.setStroke(mouseStroke);
		g2d.setColor(ctx.mouse.isPressed() ? Color.RED : Color.WHITE);

		final Point mouse = ctx.mouse.getLocation();
		g2d.drawLine(mouse.x - 5, mouse.y - 5, mouse.x + 5, mouse.y + 5);
		g2d.drawLine(mouse.x - 5, mouse.y + 5, mouse.x + 5, mouse.y - 5);
	}
}
