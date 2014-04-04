package org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Skills;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 17:36
 */
public class BrokenPipes extends RespectTask {
	//public static final int[] BROKEN_PIPE = {29410, 29411, 29413, 29414, 29761, 29762};
	public static final int SETTING_BROKEN_PIPES = 129;
	private final Pipe[] pipes = {
			new Pipe(29410, 11),
			new Pipe(29411, 12),
			new Pipe(29413, 13),
			new Pipe(29414, 14),
			new Pipe(29761, 15),
			new Pipe(29762, 16),
	};

	public BrokenPipes(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Repair pipes";
	}

	@Override
	public boolean valid() {
		return super.valid()
				&& ctx.skills.realLevel(Skills.SMITHING) >= 50
				&& getPipe() != null;
	}

	private Pipe getPipe() {
		for (Pipe pipe : pipes) {
			final GameObject object = pipe.get();
			if (object.valid()) {
				return pipe;
			}
		}
		return null;
	}

	@Override
	public void run() {
		final Pipe pipe = getPipe();
		if (pipe != null) {
			final GameObject object = pipe.get();
			if (ctx.camera.prepare(object) && object.interact("Mend")) {
				options.status = "Repairing pipe";
				options.isSmithing = false;
				sleep(1000);
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !pipe.needsFixing();
					}
				}, Random.nextInt(600, 800), Random.nextInt(8, 13));
				sleep(150);
			}
		}
	}

	class Pipe {
		private final int pipeId;
		private final int shift;

		Pipe(int pipeId, int shift) {
			this.pipeId = pipeId;
			this.shift = shift;
		}

		public boolean needsFixing() {
			return ctx.varpbits.varpbit(SETTING_BROKEN_PIPES, shift, 1) == 1;
		}

		public GameObject get() {
			if (needsFixing()) {
				return ctx.objects.select().id(pipeId).poll();
			}
			return ctx.objects.nil();
		}
	}
}
