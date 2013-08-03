package org.logicail.api.methods.providers;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Mouse;
import org.powerbot.script.util.Random;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 17:11
 */
public class MyMouse extends Mouse {
	public MyMouse(MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Move mouse to a point within the rectangle
	 *
	 * @param rectangle
	 * @return
	 */
	public boolean move(Rectangle rectangle) {
		if (rectangle.contains(ctx.mouse.getLocation())) {
			return true;
		}

		ctx.mouse.move(pointInRectangle(rectangle));

		return rectangle.contains(ctx.mouse.getLocation());
	}

	public Point pointInRectangle(Rectangle rectangle) {
		return new Point(Random.nextInt(rectangle.x + 1, rectangle.x + rectangle.width), Random.nextInt(rectangle.y + 1, rectangle.y + rectangle.height));
	}
}
