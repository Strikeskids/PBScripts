package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Random;
import org.powerbot.script.Targetable;
import org.powerbot.script.rt6.Interactive;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/12/13
 * Time: 21:19
 */
public class TargetableRectangle extends Interactive implements Targetable {
	private final Rectangle rectangle;

	public TargetableRectangle(IClientContext ctx, Rectangle rectangle) {
		super(ctx);
		this.rectangle = rectangle;
	}

	@Override
	public boolean contains(Point point) {
		return rectangle.contains(point);
	}

	@Override
	public Point nextPoint() {
		return new Point(Random.nextInt(rectangle.x + 1, rectangle.x + rectangle.width), Random.nextInt(rectangle.y + 1, rectangle.y + rectangle.height));
	}

	@Override
	public Point centerPoint() {
		return new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
	}

	@Override
	public void bounds(int i, int i2, int i3, int i4, int i5, int i6) {
	}
}
