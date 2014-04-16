package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.CropState;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.AllotmentEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.FlowerEnum;
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
		g2.setStroke(new BasicStroke(1));

		int x = 50;
		int y = 50;

		g2.setColor(COLOUR_BACKGROUND);
		g2.fillRect(x - 3, y - 3, 199, 47);
		g2.setColor(COLOUR_BACKGROUND_BORDER);
		g2.drawRect(x - 3, y - 3, 199, 47);

		AllotmentEnum.FALADOR_N.object(ctx).repaint(g2, x, y);
		HerbEnum.FALADOR.object(ctx).repaint(g2, x + 32, y);
		FlowerEnum.FALADOR.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.FALADOR_S.object(ctx).repaint(g2, x + 20, y + 16);
		x += 46;
		AllotmentEnum.CATHERBY_N.object(ctx).repaint(g2, x, y);
		HerbEnum.CATHERBY.object(ctx).repaint(g2, x + 32, y + 16);
		FlowerEnum.CATHERBY.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.CATHERBY_S.object(ctx).repaint(g2, x, y + 28);
		x += 46;
		AllotmentEnum.ARDOUGNE_N.object(ctx).repaint(g2, x, y);
		HerbEnum.ARDOUGNE.object(ctx).repaint(g2, x + 32, y + 16);
		FlowerEnum.ARDOUGNE.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.ARDOUGNE_S.object(ctx).repaint(g2, x, y + 28);
		x += 46;
		AllotmentEnum.PORT_PHASMATYS_N.object(ctx).repaint(g2, x, y);
		HerbEnum.PORT_PHASMATYS.object(ctx).repaint(g2, x + 32, y);
		FlowerEnum.PORT_PHASMATYS.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.PORT_PHASMATYS_S.object(ctx).repaint(g2, x + 20, y + 16);
		x += 46;
		HerbEnum.TROLLHEIM.object(ctx).repaint(g2, x, y);

		x = 251;

		g2.setColor(COLOUR_BACKGROUND);
		g2.fillRect(x - 3, y - 3, 75, CROP_STATES.length * 15);
		g2.setColor(COLOUR_BACKGROUND_BORDER);
		g2.drawRect(x - 3, y - 3, 75, CROP_STATES.length * 15);

		for (CropState state : CROP_STATES) {
			g2.setColor(state.color());
			g2.fillRect(x, y, 9, 9);
			g2.setColor(Color.gray);
			g2.drawRect(x, y, 9, 9);

			g2.setColor(Color.black);
			g2.drawString(state.toString(), x + 15, y + 10);
			g2.setColor(Color.gray.brighter());
			g2.drawString(state.toString(), x + 14, y + 9);

			y += 15;
		}
	}
}
