package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.powerbot.script.AbstractScript;

import java.awt.*;
import java.util.HashMap;

public class Painter extends LogicailMethodProvider {
	private static final Font FONT = new Font("Arial", Font.BOLD, 14);
	private static final Font FONT_SMALL = new Font("Arial", Font.BOLD, 12);
	private static final Color COLOUR = new Color(0, 0, 0, 64);
	private static final RenderingHints RENDERING_HINTS;

	private final String title;

	public Painter(LogicailMethodContext context, AbstractScript script) {
		super(context);
		title = script.getName() + " v" + script.getVersion();
	}

	private Point location = new Point(50, 200);

	private Rectangle rectangle = new Rectangle(location.x, location.y, 1, 1);

	public void repaint(Graphics g, String... lines) {
		Graphics2D g2d = (Graphics2D) g;

		int x = location.x + 15;
		int y = location.y + 15;

		//g.setColor(COLOUR);
		//g2d.fill(rectangle);
		//g.setColor(Color.WHITE);
		//g2d.draw(rectangle);

		g.setFont(FONT);

		strokeString(g, x, y, title);
		y += 25;

		g.setFont(FONT_SMALL);

		for (String string : lines) {
			strokeString(g, x, y, string);
			y += 15;
		}
	}

	private void strokeString(Graphics g, int x, int y, String string) {
		g.setColor(Color.BLACK);
		g.drawString(string, x - 1, y - 1);
		g.drawString(string, x - 1, y + 1);
		g.drawString(string, x + 1, y - 1);
		g.drawString(string, x + 1, y + 1);
		g.setColor(Color.WHITE);
		g.drawString(string, x, y);
	}

	static {
		RENDERING_HINTS = new RenderingHints(new HashMap());
		RENDERING_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);		/*RENDERING_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RENDERING_HINTS.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDERING_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RENDERING_HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);*/
	}
}
