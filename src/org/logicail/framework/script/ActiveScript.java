package org.logicail.framework.script;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.SimplePaint;
import org.logicail.api.providers.AnimationHistory;
import org.logicail.framework.script.state.Tree;
import org.powerbot.event.PaintListener;
import org.powerbot.script.PollingScript;

import java.awt.*;
import java.awt.event.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/07/13
 * Time: 16:51
 */
public abstract class ActiveScript extends PollingScript implements PaintListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private final Container container = new TaskContainer();
	public LogicailMethodContext ctx;
	public SimplePaint paint = null;
	protected Tree tree = null;

	protected ActiveScript() {
		getExecQueue(State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				setPaused(true);
			}
		});
		getExecQueue(State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				setPaused(false);
			}
		});
		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				shutdown();
			}
		});


		ctx = new LogicailMethodContext(this, super.ctx);

		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				submit(new AnimationHistory(ctx));
			}
		});
	}

	/**
	 * Graceful shutdown
	 */
	public final void shutdown() {
		if (!isShutdown()) {
			log.info("Shutdown");
			container.shutdown();
		}
	}

	public final boolean isShutdown() {
		return container.isShutdown();
	}

	/**
	 * Force stop
	 */
	public void forceStop() {
		container.interrupt();
	}

	public final void setPaused(boolean paused) {
		container.setPaused(paused);
	}

	public final void submit(LoopTask job) {
		container.submit(job);
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

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (paint != null) {
			paint.mouseWheelMoved(e);
		}
	}
}
