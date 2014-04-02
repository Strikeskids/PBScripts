package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:58
 */
public abstract class LoopTask<T extends LogicailScript<T>> extends Task<T> {
	private boolean paused;

	public LoopTask(T script) {
		super(script);
	}

	public final boolean isPaused() {
		return paused;
	}

	@Override
	public void run() {
		while (!ctx.isShutdown()) {
			if (ctx.isPaused()) {
				paused = true;
				ctx.sleep(1000);
			} else {
				paused = false;

				int delay;
				try {
					delay = loop();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
					delay = -1;
				}

				if (delay >= 0) {
					ctx.sleep(delay);
				} else if (delay == -1) {
					break;
				}
			}
		}
	}

	public abstract int loop();
}
