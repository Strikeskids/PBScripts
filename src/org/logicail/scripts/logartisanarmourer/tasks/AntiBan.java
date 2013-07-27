package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.framework.script.job.LoopTask;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 21:53
 */
public class AntiBan extends LoopTask {
	private final Timer timer = new Timer(Random.nextInt(5000, 45000));

	public AntiBan(MyMethodContext ctx) {
		super(ctx);
	}

	@Override
	public int loop() {
		if (!timer.isRunning()) {
			timer.setEndIn(Random.nextInt(5000, 45000));

			final Dimension dimension = ctx.game.getDimensions();
			switch (Random.nextInt(0, 5)) {
				case 0:
					//ctx.camera.setYaw(Random.nextBoolean() ? Random.nextInt(0, 360) : Random.nextInt(-359, 0));
					break;
				case 1:
					ctx.mouse.move(Random.nextInt(0, (int) (dimension.getWidth() + 1)), Random.nextInt(0, (int) (dimension.getHeight() + 1)));
					break;
				case 2:
					switch (Random.nextInt(0, 4)) {
						case 0:
							ctx.mouse.move(0, Random.nextInt(0, (int) (dimension.getHeight() + 1)));
							break;
						case 1:
							ctx.mouse.move((int) dimension.getWidth(), Random.nextInt(0, (int) (dimension.getHeight() + 1)));
							break;
						case 2:
							ctx.mouse.move(Random.nextInt(0, (int) (dimension.getWidth() + 1)), 0);
							break;
						case 3:
							ctx.mouse.move(Random.nextInt(0, (int) (dimension.getWidth() + 1)), (int) dimension.getHeight());
							break;
					}
					break;
				case 3:
					for (GameObject object : ctx.objects.select(new Filter<GameObject>() {
						@Override
						public boolean accept(GameObject gameObject) {
							return Random.nextBoolean() && gameObject.isOnScreen();
						}
					}).shuffle().first()) {
						if (Random.nextBoolean()) {
							object.interact("Examine");
						} else {
							object.click(false);
						}
					}
					break;

				default:
					Rectangle rectangle = new Rectangle(dimension);
					Point2D p = null;
					while (p == null || ctx.mouse.getLocation().distance(p) < 250) {
						p = ctx.mouse.pointInRectangle(rectangle);
					}
					ctx.mouse.move((int) p.getX(), (int) p.getY());

					break;
			}
		}
		return Random.nextInt(500, 1500);
	}
}
