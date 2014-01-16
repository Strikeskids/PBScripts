package org.logicail.rsbot.scripts.framework;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Tree;
import org.logicail.rsbot.util.LinkedProperties;
import org.logicail.rsbot.util.Painter;
import org.powerbot.event.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:07
 */
public abstract class LogicailScript<T extends LogicailScript> extends PollingScript implements PaintListener {
	public final IMethodContext ctx;
	public final Tree<T> tree;
	private final Painter paint;
	protected LogicailGui<T> gui;

	protected LogicailScript() {
		this.ctx = new IMethodContext(super.ctx, this);

		tree = new Tree<T>((T) this);
		paint = new Painter(ctx, this);

		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				try {
					if (gui != null) {
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
		return Random.nextInt(50, 500);
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
	/*public void submit(Runnable task) {
		ctx.submit(task);
	}*/
}
