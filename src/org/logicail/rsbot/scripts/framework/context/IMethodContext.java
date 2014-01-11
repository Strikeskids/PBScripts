package org.logicail.rsbot.scripts.framework.context;

import com.sk.methods.Combat;
import com.sk.methods.SkKeyboard;
import com.sk.methods.action.ActionBar;
import org.logicail.rsbot.scripts.framework.context.providers.*;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:53
 */
public class IMethodContext extends MethodContext {
	public final Logger log;

	public boolean debug;

	// Providers
	public ISkillingInterface skillingInterface;
	public IBackpack backpack;
	public ICamera camera;
	public IEquipment equipment;
	public ISummoning summoning;
	public IChat chat;
	public ILodestone lodestones;
	public IMovement movement;
	public IMagic magic;
	public IBank bank;

	// SK
	public SkKeyboard keyboard;
	public ActionBar actionBar;
	public Combat combat;

	public final String useragent;
	// SK

	// Concurrent task executor
	//private final ExecutorService executor = Executors.newCachedThreadPool();
	private final AbstractScript script;
	private volatile boolean shutdown;
	private volatile boolean paused;

	public IMethodContext(final MethodContext originalContext, AbstractScript script) {
		super(originalContext);
		this.script = script;
		log = script.log;

		useragent = script.getName().toUpperCase().replaceAll(" ", "_") + "/" + script.getVersion();
/*
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
*/
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

	public final boolean isPaused() {
		return paused;
	}

	public final boolean isShutdown() {
		return shutdown;
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
/*
	public final void submit(Runnable task) {
		if (!isShutdown()) {
			executor.submit(task);
		}
	}
	*/
}