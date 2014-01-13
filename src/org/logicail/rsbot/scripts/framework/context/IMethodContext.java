package org.logicail.rsbot.scripts.framework.context;

import com.sk.methods.Combat;
import com.sk.methods.SkKeyboard;
import com.sk.methods.action.ActionBar;
import org.logicail.rsbot.scripts.framework.context.providers.*;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.Script;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:53
 */
public class IMethodContext extends MethodContext {
	public final ILogger log = new ILogger(10);

	// Providers
	public final ISkillingInterface skillingInterface;
	public final IBackpack backpack;
	public final ICamera camera;
	public final IEquipment equipment;
	public final ISummoning summoning;
	public final IChat chat;
	public final ILodestone lodestones;
	public final IMovement movement;
	public final IMagic magic;
	public final IBank bank;

	// SK
	public final SkKeyboard keyboard;
	public final ActionBar actionBar;
	public final Combat combat;
	// SK

	public final String useragent;

	// Concurrent task executor
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final AbstractScript script;
	private final AtomicBoolean paused = new AtomicBoolean();
	private final AtomicBoolean shutdown = new AtomicBoolean();

	public IMethodContext(final MethodContext originalContext, AbstractScript script) {
		super(originalContext);
		this.script = script;
		script.log.addHandler(log);

		useragent = script.getName().toUpperCase().replaceAll(" ", "_") + "/" + script.getVersion();

		script.getExecQueue(Script.State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				paused.set(true);
			}
		});

		script.getExecQueue(Script.State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				paused.set(false);
			}
		});

		script.getExecQueue(Script.State.STOP).add(new Runnable() {
			@Override
			public void run() {
				paused.set(true);
				shutdown.set(true);
				executor.shutdown();
			}
		});

		skillingInterface = new ISkillingInterface(this);
		backpack = new IBackpack(this);
		camera = new ICamera(this);
		equipment = new IEquipment(this);
		summoning = new ISummoning(this);
		chat = new IChat(this);
		lodestones = new ILodestone(this);

		/*super.keyboard =*/
		this.keyboard = new SkKeyboard(this);
		actionBar = new ActionBar(this);
		combat = new Combat(this);

		magic = new IMagic(this);
		movement = new IMovement(this);
		bank = new IBank(this);
	}

	public boolean isPaused() {
		return paused.get();
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
						new ErrorDialog(title, reason);
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

	public final void submit(Runnable task) {
		if (!isShutdown()) {
			executor.submit(task);
		}
	}

	public boolean isShutdown() {
		return shutdown.get();
	}
}
