package org.logicail.rsbot.scripts.testing;

import org.powerbot.script.*;
import org.powerbot.script.rt4.BoundingModel;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Interactive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 22:47
 */
@Script.Manifest(name = "BoundsUtil", description = "Bounds Util for GameObjects since RSBot is broken")
public class BoundsUtil extends PollingScript<ClientContext> implements PaintListener, KeyListener {
	private AtomicReference<GameObject> objectAtomicReference = new AtomicReference<GameObject>(null);
	private AtomicBoolean selecting = new AtomicBoolean();

	@Override
	public void poll() {
		Condition.sleep(100);
	}

	private GameObject getGameObject(final Point pressLocation) {
		return ctx.objects.select().select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject o) {
				return (o.type() == GameObject.Type.INTERACTIVE || o.type() == GameObject.Type.BOUNDARY) && o.contains(pressLocation);
			}
		}).poll();
	}

	@Override
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new BoundsUtilFrame(BoundsUtil.this);
			}
		});
	}

	public void bounds(int value, int value1, int value2, int value3, int value4, int value5) {
		final GameObject object = objectAtomicReference.get();
		if (object != null) {
			object.bounds(value, value1, value2, value3, value4, value5);
		}
	}

	public static BoundingModel getBoundingModel(Interactive object) throws IllegalAccessException, NoSuchFieldException {
		final Field field = Interactive.class.getDeclaredField("boundingModel");
		field.setAccessible(true);
		final AtomicReference<BoundingModel> model = (AtomicReference<BoundingModel>) field.get(object);
		return model.get();
	}

	public void select() {
		selecting.set(true);
	}

	@Override
	public void repaint(Graphics graphics) {
		if (selecting.get()) {
			final GameObject object = getGameObject(ctx.input.getLocation());
			graphics.drawString(String.valueOf(object), 50, 50);
			if (object.valid()) {
				try {
					getBoundingModel(object).drawWireFrame(graphics);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
		} else {
			final GameObject object = objectAtomicReference.get();
			if (object != null) {
				if (object.valid()) {
					try {
						getBoundingModel(object).drawWireFrame(graphics);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			final GameObject object = getGameObject(ctx.input.getLocation());
			if (object.valid()) {
				selecting.set(false);
				objectAtomicReference.set(object);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
