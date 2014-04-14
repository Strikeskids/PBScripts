package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
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

	@Override
	public void repaint(Graphics graphics) {
		Graphics2D g2 = (Graphics2D) graphics;
		HerbEnum.FALADOR.herb(ctx).repaint(g2, 50, 50);
	}
}
