package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.CropState;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.AllotmentEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.HerbEnum;
import org.powerbot.script.PaintListener;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/04/2014
 * Time: 19:40
 */
public class FarmingPaint extends IClientAccessor implements PaintListener {
	public FarmingPaint(IClientContext ctx) {
		super(ctx);
	}

	private static final CropState[] CROP_STATES = CropState.values();
	private static final Color COLOUR_BACKGROUND = new Color(0, 0, 0, 128);
	private static final Color COLOUR_BACKGROUND_BORDER = new Color(Color.gray.getRed(), Color.gray.getGreen(), Color.gray.getBlue(), 128);

	@Override
	public void repaint(Graphics graphics) {
		Graphics2D g2 = (Graphics2D) graphics;

		int x = 50;
		int y = 50;


		g2.setStroke(new BasicStroke(1));
		g2.setColor(COLOUR_BACKGROUND);
		g2.fillRect(x - 3, y - 3, 198, 42);
		g2.setColor(COLOUR_BACKGROUND_BORDER);
		g2.drawRect(x - 3, y - 3, 198, 42);

		HerbEnum.FALADOR.herb(ctx).repaint(g2, x + 26, y);
		AllotmentEnum.FALADOR_N.allotment(ctx).repaint(g2, x, y);
		AllotmentEnum.FALADOR_S.allotment(ctx).repaint(g2, x + 17, y + 14);
		x += 46;
		AllotmentEnum.CATHERBY_N.allotment(ctx).repaint(g2, x, y);
		HerbEnum.CATHERBY.herb(ctx).repaint(g2, x + 26, y + 14);
		AllotmentEnum.CATHERBY_S.allotment(ctx).repaint(g2, x, y + 24);
		x += 46;
		AllotmentEnum.ARDOUGNE_N.allotment(ctx).repaint(g2, x, y);
		HerbEnum.ARDOUGNE.herb(ctx).repaint(g2, x + 26, y + 14);
		AllotmentEnum.ARDOUGNE_S.allotment(ctx).repaint(g2, x, y + 24);
		x += 46;
		HerbEnum.PORT_PHASMATYS.herb(ctx).repaint(g2, x + 26, y);
		AllotmentEnum.PORT_PHASMATYS_N.allotment(ctx).repaint(g2, x, y);
		AllotmentEnum.PORT_PHASMATYS_S.allotment(ctx).repaint(g2, x + 17, y + 14);
		x += 46;
		HerbEnum.TROLLHEIM.herb(ctx).repaint(g2, x, y);

		x = 500;
		y = 20;

		g2.setColor(COLOUR_BACKGROUND);
		g2.fillRect(x - 6, y - 6, 78, CROP_STATES.length * 16);
		g2.setColor(COLOUR_BACKGROUND_BORDER);
		g2.drawRect(x - 6, y - 6, 78, CROP_STATES.length * 16);

		g2.setStroke(new BasicStroke(1));
		for (CropState state : CROP_STATES) {
			g2.setColor(state.color());
			g2.fillRect(x, y, 6, 6);
			g2.setColor(Color.gray);
			g2.drawRect(x, y, 6, 6);

			g2.setColor(Color.BLACK);
			g2.drawString(state.toString(), x + 15, y + 9);
			g2.setColor(Color.WHITE);
			g2.drawString(state.toString(), x + 14, y + 8);

			y += 15;
		}
	}
}
