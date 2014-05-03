package org.logicail.rsbot.scripts.framework.context;

import com.sk.methods.Combat;
import com.sk.methods.SkKeyboard;
import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.context.providers.*;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.scripts.testing.FarmingTest;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.ClientContext;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:53
 */
public class IClientContext extends ClientContext {
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
	public final IFarming farming;
	// SK
	public final SkKeyboard keyboard;
	//public final ActionBar actionBar;
	public final Combat combat;
	// SK
	public final String useragent;
	public final LogicailScript script;
	// Concurrent task executor
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final AtomicBoolean paused = new AtomicBoolean();
	private final AtomicBoolean shutdown = new AtomicBoolean();
	public final ClientContext original;

	public IClientContext(final ClientContext originalContext, LogicailScript script) {
		super(originalContext);
		this.original = originalContext;
		this.script = script;
		script.log.addHandler(log);

		useragent = script.getName().toUpperCase().replaceAll(" ", "_") + "/" + script.version();

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

		if(script instanceof FarmingTest) {
			farming = new IFarming(this);
		} else {
			farming = null;
		}

		/*super.keyboard =*/
		this.keyboard = new SkKeyboard(this);
		//actionBar = new ActionBar(this);
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
			while (t.running() && game.loggedIn()) {
				bank.close();

				game.logout(lobby);

				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !game.loggedIn();
					}
				}, 100, 10);
			}
		} catch (Exception ignored) {
		} finally {
			controller.stop();
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

	public void sleep(int millis) {
		try {
			Thread.sleep(Math.max(5, (int) (millis * Random.nextDouble(0.75, 1.5))));
		} catch (InterruptedException ignored) {
		}
	}
}
