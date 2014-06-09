package org.logicail.rsbot.scripts.framework.tasks.impl;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.framework.util.Timer;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.GameObject;

import java.awt.*;

/**
 * User: logicail
 * Date: 25/05/12
 * Time: 09:46
 */
public class AntiBan<T extends LogicailScript<T>> extends Node<T> {
	private final Timer nextAntiBan = new Timer(Random.nextInt(5000, 45000));

	public AntiBan(T script) {
		super(script);
	}

	@Override
	public boolean valid() {
		return ctx.game.loggedIn() && !nextAntiBan.running();
	}

	private void wiggleMouse() {
		final Point currentPoint = ctx.input.getLocation();
		ctx.input.move(new Point(currentPoint.x + Random.nextInt(-250, 251), currentPoint.y + Random.nextInt(-250, 251)));
	}

	@Override
	public void run() {
		nextAntiBan.endIn(Random.nextInt(5000, 45000));

		final Dimension dimension = ctx.game.dimensions();
		switch (Random.nextInt(0, 5)) {
			case 0:
				ctx.camera.angle(Random.nextBoolean() ? Random.nextInt(0, 360) : Random.nextInt(-359, 0));
				break;
			case 1:
				ctx.input.move(Random.nextInt(0, (int) (dimension.getWidth() + 1)), Random.nextInt(0, (int) (dimension.getHeight() + 1)));
				break;
			case 2:
				//Util.mouseOffScreen();
				ctx.input.defocus();
				ctx.sleep(5000);
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
		ctx.sleep(100);
	}
}