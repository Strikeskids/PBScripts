package org.logicail.framework.script;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/08/13
 * Time: 13:03
 */
public abstract class LoopTask extends LogicailMethodProvider implements Runnable {
	private boolean paused;

	public LoopTask(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public void run() {
		while (!ctx.isShutdown()) {
			if (ctx.isPaused()) {
				paused = true;
				sleep(1000);
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
					sleep(delay);
				} else if (delay == -1) {
					break;
				}
			}
		}
	}

	public abstract int loop();
}
