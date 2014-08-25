package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.CropState;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.*;
import org.powerbot.script.PaintListener;

import java.awt.*;
import java.io.Serializable;

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

	private static final int HEIGHT = 250;
	private static final int WIDTH = 184;
	private static final int X = 110;
	private static final int Y = 10;

	@Override
	public void repaint(Graphics graphics) {
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setStroke(new BasicStroke(1));

		g2.setFont(g2.getFont().deriveFont(10f));

		int x = X;
		int y = Y;

		g2.setColor(COLOUR_BACKGROUND);
		g2.fillRect(x - 2, y - 2, WIDTH, HEIGHT);
		g2.setColor(COLOUR_BACKGROUND_BORDER);
		g2.drawRect(x - 2, y - 2, WIDTH, HEIGHT);

		y += 8;
		text(g2, "Falador", x, y);
		y += 2;
		x += 1;
		AllotmentEnum.FALADOR_N.object(ctx).repaint(g2, x, y);
		HerbEnum.FALADOR.object(ctx).repaint(g2, x + 32, y);
		FlowerEnum.FALADOR.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.FALADOR_S.object(ctx).repaint(g2, x + 20, y + 16);
		CompostEnum.FALADOR.object(ctx).repaint(g2, x + 24, y);

		y += 51;
		text(g2, "Catherby", x, y);
		y += 4;
		AllotmentEnum.CATHERBY_N.object(ctx).repaint(g2, x, y);
		HerbEnum.CATHERBY.object(ctx).repaint(g2, x + 32, y + 16);
		FlowerEnum.CATHERBY.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.CATHERBY_S.object(ctx).repaint(g2, x, y + 28);
		CompostEnum.CATHERBY.object(ctx).repaint(g2, x, y + 18);

		y += 51;
		text(g2, "Ardougne", x, y);
		y += 4;
		AllotmentEnum.ARDOUGNE_N.object(ctx).repaint(g2, x, y);
		HerbEnum.ARDOUGNE.object(ctx).repaint(g2, x + 32, y + 16);
		FlowerEnum.ARDOUGNE.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.ARDOUGNE_S.object(ctx).repaint(g2, x, y + 28);
		CompostEnum.ARDOUGNE.object(ctx).repaint(g2, x, y + 18);

		y += 51;
		text(g2, "Port", x, y);
		y += 8;
		text(g2, "Phasmatys", x, y);
		y += 4;
		AllotmentEnum.PORT_PHASMATYS_N.object(ctx).repaint(g2, x, y);
		HerbEnum.PORT_PHASMATYS.object(ctx).repaint(g2, x + 32, y);
		FlowerEnum.PORT_PHASMATYS.object(ctx).repaint(g2, x + 16, y + 16);
		AllotmentEnum.PORT_PHASMATYS_S.object(ctx).repaint(g2, x + 20, y + 16);
		CompostEnum.PORT_PHASMATYS.object(ctx).repaint(g2, x + 44, y + 28);

		y += 51;
		text(g2, "Trollheim", x, y);
		y += 2;
		HerbEnum.TROLLHEIM.object(ctx).repaint(g2, x, y);


		x = X + 57;
		y = Y + 8;
		text(g2, "Trees", x, y);
		y += 2;

		for (TreeEnum treeEnum : TreeEnum.values()) {
			treeEnum.object(ctx).repaint(g2, x, y);
			text(g2, treeEnum, x + 12, y + 8);
			y += 12;
		}

		y += 7;
		text(g2, "Fruit Trees", x - 1, y);
		y += 2;

		for (FruitTreeEnum treeEnum : FruitTreeEnum.values()) {
			treeEnum.object(ctx).repaint(g2, x, y);
			text(g2, treeEnum, x + 12, y + 8);
			y += 12;
		}

		y += 7;
		text(g2, "Spirit Trees", x - 1, y);
		y += 4;

		for (SpiritTreeEnum treeEnum : SpiritTreeEnum.values()) {
			treeEnum.object(ctx).repaint(g2, x, y);
			text(g2, treeEnum, x + 12, y + 8);
			y += 12;
		}

		y += 7;
		text(g2, "Cactus", x - 1, y);
		y += 2;

		for (CactusEnum treeEnum : CactusEnum.values()) {
			treeEnum.object(ctx).repaint(g2, x, y);
			text(g2, treeEnum, x + 12, y + 8);
			y += 12;
		}

		y += 7;
		text(g2, "Calquat", x - 1, y);
		y += 4;

		for (CalquatEnum treeEnum : CalquatEnum.values()) {
			treeEnum.object(ctx).repaint(g2, x, y);
			text(g2, treeEnum, x + 12, y + 8);
			y += 12;
		}

		y = Y;
		x = X - 64;
		g2.setColor(COLOUR_BACKGROUND);
		g2.fillRect(x - 2, y - 2, 62, CROP_STATES.length * 12 + 3);
		g2.setColor(COLOUR_BACKGROUND_BORDER);
		g2.drawRect(x - 2, y - 2, 62, CROP_STATES.length * 12 + 3);

		++x;
		++y;

		for (CropState state : CROP_STATES) {
			g2.setColor(state.color());
			g2.fillRect(x, y, 9, 9);
			g2.setColor(Color.gray);
			g2.drawRect(x, y, 9, 9);

			text(g2, state, x + 12, y + 9);

			y += 12;
		}
	}

	private void text(Graphics2D g2, Serializable text, int x, int y) {
		g2.setColor(Color.black);
		g2.drawString(text.toString(), x + 1, y + 1);
		g2.setColor(Color.gray.brighter());
		g2.drawString(text.toString(), x, y);
	}
}
