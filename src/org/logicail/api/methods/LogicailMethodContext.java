package org.logicail.api.methods;

import org.logicail.api.methods.providers.*;
import org.logicail.framework.script.LoopTask;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.Script;
import org.powerbot.script.methods.MethodContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:56
 */
public class LogicailMethodContext extends MethodContext {
	private final ExecutorService executor;
	public Interaction interaction;
	public SkillingInterface skillingInterface;
	public MyBackpack backpack;
	public Waiting waiting;
	public MyMouse mouse;
	public MyMovement movement;
	public AbstractScript script;
	public AnimationHistory animationHistory;
	public ChatOptions chatOptions;
	public MyWidgets widgets;
	private volatile boolean shutdown;
	private volatile boolean paused;

	public LogicailMethodContext(MethodContext original, AbstractScript script) {
		super(original.getBot());

		this.script = script;
		this.executor = Executors.newCachedThreadPool();

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

	@Override
	public void init(MethodContext ctx) {
		super.init(ctx);
		interaction = new Interaction(this);
		skillingInterface = new SkillingInterface(this);
		super.backpack = backpack = new MyBackpack(this);
		waiting = new Waiting(this);
		super.mouse = mouse = new MyMouse(this);
		super.movement = movement = new MyMovement(this);
		animationHistory = new AnimationHistory(this);
		chatOptions = new ChatOptions(this);
		widgets = new MyWidgets(this);
	}

	public final boolean isShutdown() {
		return shutdown;
	}

	public final boolean isPaused() {
		return paused;
	}

	public final void submit(LoopTask loopTask) {
		if (!isShutdown()) {
			executor.submit(loopTask);
		}
	}
}
