package org.logicail.rsbot.scripts.framework;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.tasks.Tree;
import org.logicail.rsbot.util.LinkedProperties;
import org.logicail.rsbot.util.Painter;
import org.powerbot.event.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:07
 */
public abstract class LogicailScript<T extends LogicailScript> extends PollingScript implements PaintListener {
	public final LogicailMethodContext ctx;
	public final Tree<T> tree;
	protected JFrame gui;
	private final Painter paint;

	protected LogicailScript() {
		this.ctx = new LogicailMethodContext(super.ctx, this);
		this.ctx.init(super.ctx);
		tree = new Tree<T>((T) this);
		paint = new Painter(ctx, this);

		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				try {
					if (gui != null && gui.isVisible()) {
						gui.dispose();
					}
				} catch (Exception ignored) {
				}
			}
		});
	}

	/**
	 * Ordered properties to show on paint box
	 *
	 * @return
	 */
	public abstract LinkedProperties getPaintInfo();

	@Override
	public int poll() {
		try {
			if (ctx.game.getClientState() == Game.INDEX_MAP_LOADED) {
				tree.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Random.nextInt(150, 650);
	}

	@Override
	public void repaint(Graphics g) {
		paint.properties(getPaintInfo()).repaint(g);
	}

	/**
	 * Submit task to executor (runs separate to script poll)
	 *
	 * @param task
	 */
	public void submit(Task<T> task) {
		ctx.submit(task);
	}
}
