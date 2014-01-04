package org.logicail.rsbot.scripts.framework.tasks.impl;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.LoopTask;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;

import java.awt.*;

/**
 * User: logicail
 * Date: 25/05/12
 * Time: 09:46
 */
public class AntiBan<T extends LogicailScript<T>> extends LoopTask<T> {
	private final Timer nextAntiBan = new Timer(Random.nextInt(5000, 45000));

	public AntiBan(T script) {
		super(script);
	}

	@Override
	public int loop() {
		if (ctx.game.isLoggedIn() && !nextAntiBan.isRunning()) {
			nextAntiBan.setEndIn(Random.nextInt(5000, 45000));

			final Dimension dimension = ctx.game.getDimensions();
			switch (Random.nextInt(0, 5)) {
				case 0:
					ctx.camera.setAngle(Random.nextBoolean() ? Random.nextInt(0, 360) : Random.nextInt(-359, 0));
					break;
				case 1:
					ctx.mouse.move(Random.nextInt(0, (int) (dimension.getWidth() + 1)), Random.nextInt(0, (int) (dimension.getHeight() + 1)));
					break;
				case 2:
					//Util.mouseOffScreen();
					break;
				case 3:
					for (GameObject object : ctx.objects.select().select(new Filter<GameObject>() {
						@Override
						public boolean accept(GameObject gameObject) {
							return Random.nextInt(0, 10) == 0;
						}
					}).first()) {
						ctx.camera.turnTo(object, 20);
					}
					break;

				default:
					wiggleMouse();
					break;
			}
			sleep(50, 150);
		}
		return Random.nextInt(500, 1500);
	}

	private void wiggleMouse() {
		final Point currentPoint = ctx.mouse.getLocation();
		ctx.mouse.move(new Point(currentPoint.x + Random.nextInt(-250, 251), currentPoint.y + Random.nextInt(-250, 251)));
	}
}