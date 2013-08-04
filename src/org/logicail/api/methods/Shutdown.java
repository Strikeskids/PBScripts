package org.logicail.api.methods;

import org.powerbot.script.util.Timer;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 21:42
 */
public class Shutdown extends LogicailMethodProvider {
	public Shutdown(LogicailMethodContext context) {
		super(context);
	}

	public void stop(final String reason) {
		stop(reason, "Error", true);
	}

	public void stop(final String reason, boolean lobby) {
		stop(reason, "Error", lobby);
	}

	public void stop(final String reason, final String title, boolean lobby) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						new ErrorDialog(null, title, reason);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			});
			Timer t = new Timer(10000);
			while (t.isRunning() && ctx.game.isLoggedIn()) {
				ctx.bank.close();
				//ctx.summoning.close();

				// Bank pin
				ctx.game.logout(lobby);

				sleep(1000, 3000);
			}
		} catch (Exception ignored) {
		}

		ctx.getBot().stop();
	}
}
