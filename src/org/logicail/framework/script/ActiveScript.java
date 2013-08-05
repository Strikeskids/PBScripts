package org.logicail.framework.script;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.SimplePaint;
import org.logicail.api.methods.providers.AnimationHistory;
import org.logicail.framework.script.state.Tree;
import org.powerbot.event.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/07/13
 * Time: 16:51
 */
public abstract class ActiveScript extends PollingScript implements PaintListener, MouseListener, MouseMotionListener/*, MouseWheelListener*/ {
	public LogicailMethodContext ctx;
	public SimplePaint paint = null;
	protected Tree tree = null;

	protected ActiveScript() {
		this.ctx = new LogicailMethodContext(super.ctx, this);

		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				ctx.submit(new AnimationHistory(ctx));
			}
		});
	}

	@Override
	public int poll() {
		try {
			if (tree != null && ctx.game.getClientState() == Game.INDEX_MAP_LOADED) {
				if (tree.activate()) {
					tree.execute();
					if (Random.nextBoolean() && Random.nextBoolean()) {
						return Random.nextInt(500, 1000);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Random.nextInt(100, 500);
	}

	@Override
	public void repaint(Graphics g) {
		if (paint != null) {
			paint.draw(g);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (paint != null) {
			paint.mouseClicked(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (paint != null) {
			paint.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (paint != null) {
			paint.mouseReleased(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (paint != null) {
			paint.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (paint != null) {
			paint.mouseExited(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (paint != null) {
			paint.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (paint != null) {
			paint.mouseMoved(e);
		}
	}

	/*@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (paint != null) {
			paint.mouseWheelMoved(e);
		}
	}*/

	public final void shutdown() {
		ctx.getBot().stop();
	}

	@Override
	public void setContext(MethodContext ctx) {
		this.ctx.init(ctx);
	}
}
