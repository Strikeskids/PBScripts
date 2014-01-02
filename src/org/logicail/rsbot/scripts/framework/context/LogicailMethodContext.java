package org.logicail.rsbot.scripts.framework.context;

import org.logicail.rsbot.scripts.framework.context.providers.MyBackPack;
import org.logicail.rsbot.scripts.framework.context.providers.MyCamera;
import org.logicail.rsbot.scripts.framework.context.providers.SkillingInterface;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.Script;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:53
 */
public class LogicailMethodContext extends MethodContext {
	public final Logger log;

	// Providers
	public SkillingInterface skillingInterface;
	public MyBackPack backpack;
	public MyCamera camera;
	// Concurrent task executor
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final AbstractScript script;
	private volatile boolean shutdown;
	private volatile boolean paused;

	private final String useragent;

	public LogicailMethodContext(final MethodContext originalContext, AbstractScript script) {
		super(originalContext.getBot());
		this.script = script;
		log = script.log;

		useragent = script.getName().toUpperCase() + "/" + script.getVersion();

		script.getExecQueue(Script.State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				paused = true;
			}
		});

		script.getExecQueue(Script.State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				paused = false;
			}
		});

		script.getExecQueue(Script.State.STOP).add(new Runnable() {
			@Override
			public void run() {
				shutdown = true;
				executor.shutdown();
			}
		});
	}

	public final boolean isPaused() {
		return paused;
	}

	public final boolean isShutdown() {
		return shutdown;
	}

	@Override
	public void init(MethodContext context) {
		super.init(context);

		skillingInterface = new SkillingInterface(this);
		backpack = new MyBackPack(this);
		camera = new MyCamera(this);
	}

	public void stop(final String reason) {
		stop(reason, "Error", true);
	}

	public void stop(final String reason, boolean lobby) {
		stop(reason, "Error", lobby);
	}

	public void stop(final String reason, final String title, boolean lobby) {
		try {
			EventQueue.invokeLater(new Runnable() {
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
			while (t.isRunning() && game.isLoggedIn()) {
				bank.close();

				// Bank pin
				Component bankPinCloseButton = widgets.get(13, 25);
				if (bankPinCloseButton.isValid()) {
					bankPinCloseButton.click(true);
				}

				game.logout(lobby);

				game.sleep(1000, 3000);
			}
		} catch (Exception ignored) {
		} finally {
			script.getController().stop();
		}
	}

	public final void submit(Task task) {
		if (!isShutdown()) {
			executor.submit(task);
		}
	}
}
