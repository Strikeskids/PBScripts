package org.logicail.rsbot.scripts.framework.context.rt6;

import com.logicail.accessors.RT6DefinitionManager;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.*;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.ClientContext;

import java.awt.*;
import java.io.FileNotFoundException;
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
	public final ILogger log;
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
	public final RT6DefinitionManager definitions;
	public final ComponentQuery components;

	public String useragent;
	// Concurrent task executor
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final AtomicBoolean paused = new AtomicBoolean();
	private final AtomicBoolean shutdown = new AtomicBoolean();
	public final ClientContext original;

	public IClientContext(final ClientContext original) {
		super(original);
		this.original = original;
		log = new ILogger(10);

		controller.script().getExecQueue(Script.State.START).add(new Runnable() {
			@Override
			public void run() {
				useragent = controller.script().getName().toUpperCase().replaceAll(" ", "_") + "/" + controller.script().getProperties().get("version");
			}
		});

		controller.script().getExecQueue(Script.State.START).add(new Runnable() {
			@Override
			public void run() {
				controller.script().log.addHandler(log);
			}
		});

		controller.script().getExecQueue(Script.State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				paused.set(true);
			}
		});

		controller.script().getExecQueue(Script.State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				paused.set(false);
			}
		});

		controller.script().getExecQueue(Script.State.STOP).add(new Runnable() {
			@Override
			public void run() {
				paused.set(true);
				shutdown.set(true);
				executor.shutdown();
			}
		});

		controller.script().getExecQueue(Script.State.STOP).add(new Runnable() {
			@Override
			public void run() {
				controller.script().log.removeHandler(log);
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
		//actionBar = new ActionBar(this);

		magic = new IMagic(this);
		movement = new IMovement(this);
		bank = new IBank(this);
		farming = new IFarming(this);

		controller.script().getExecQueue(Script.State.START).add(new Runnable() {
			@Override
			public void run() {
				controller.script().log.info("Loading cache from " + RT6DefinitionManager.directory());
			}
		});

		RT6DefinitionManager manger = null;

		try {
			manger = new RT6DefinitionManager(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.controller.stop();
		}

		this.definitions = manger;
		this.components = new ComponentQuery(this);
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
