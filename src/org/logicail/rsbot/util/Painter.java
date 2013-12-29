package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.powerbot.event.PaintListener;
import org.powerbot.script.AbstractScript;

import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;

public class Painter extends LogicailMethodProvider implements PaintListener {
	public static Font FONT = new Font("Arial", Font.BOLD, 14);
	public static Font FONT_SMALL = new Font("Arial", Font.BOLD, 12);
	private static final Color COLOUR_BACKGROUND = new Color(0, 0, 0, 64);
	private static final Color COLOUR_BORDER = new Color(255, 255, 255, 192);
	private static final RenderingHints RENDERING_HINTS;
	private LinkedProperties properties;
	private BasicStroke mouseStroke = new BasicStroke(2f);

	private final String title;

	public Painter(LogicailMethodContext context, AbstractScript script) {
		super(context);
		title = String.format("%s v%s", script.getName(), script.getVersion());
	}

	private Point location = new Point(50, 200);

	private Rectangle rectangle = new Rectangle(location.x, location.y, 1, 1);

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
		g2d.fill(rectangle);
		g.setColor(COLOUR_BORDER);
		g2d.draw(rectangle);

		g.setFont(FONT);
		FontMetrics fontMetrics = g.getFontMetrics();
		width = Math.max(width, fontMetrics.stringWidth(title));
		g.drawString(title, x, y);
		y += 25;

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

		// Draw mouse
		g2d.setStroke(mouseStroke);
		g.setColor(ctx.mouse.isPressed() ? Color.RED : COLOUR_BACKGROUND.WHITE);
		final Point mouse = ctx.mouse.getLocation();
		g.drawLine(mouse.x - 5, mouse.y - 5, mouse.x + 5, mouse.y + 5);
		g.drawLine(mouse.x - 5, mouse.y + 5, mouse.x + 5, mouse.y - 5);

		// Resize background
		rectangle.width = width + 15;
		rectangle.height = y - location.y;
	}

/*	private void strokeString(Graphics g, int x, int y, String string) {
		g.setColor(Color.BLACK);
		g.drawString(string, x - 1, y - 1);
		g.drawString(string, x - 1, y + 1);
		g.drawString(string, x + 1, y - 1);
		g.drawString(string, x + 1, y + 1);
		g.setColor(Color.WHITE);
		g.drawString(string, x, y);
	}*/

	static {
		RENDERING_HINTS = new RenderingHints(new HashMap());
		RENDERING_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);		/*RENDERING_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RENDERING_HINTS.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDERING_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RENDERING_HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);*/
	}
}
