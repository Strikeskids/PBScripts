package org.logicail.rsbot.util;

import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Targetable;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/12/13
 * Time: 21:19
 */
public class TargetableRectangle implements Targetable {
	private Rectangle rectangle;

	public TargetableRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	@Override
	public boolean contains(Point point) {
		return rectangle.contains(point);
	}

	@Override
	public Point getCenterPoint() {
		return new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
	}

	@Override
	public Point getInteractPoint() {
		return getNextPoint();
	}

	@Override
	public Point getNextPoint() {
		return new Point(Random.nextInt(rectangle.x + 1, rectangle.x + rectangle.width), Random.nextInt(rectangle.y + 1, rectangle.y + rectangle.height));
	}
}
