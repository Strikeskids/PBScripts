package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.respect;

import org.logicail.cache.loader.rt6.wrapper.ObjectDefinition;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
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
	public static final int[] BROKEN_PIPE = {29410, 29411, 29413, 29414, 29761, 29762};

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
				&& getPipe() > -1;
	}

	private int getPipe() {
		return ctx.objects.select().id(BROKEN_PIPE).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return isBroken(gameObject.id());
			}
		}).nearest().peek().id();
	}

	private boolean isBroken(int id) {
		ObjectDefinition definition = ctx.definitions.object(id);
		if (definition != null) {
			ObjectDefinition child = definition.child(ctx);
			if (child != null && child.name.equals("Burst pipe")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void run() {
		final GameObject pipe = ctx.objects.poll();
		if (pipe.valid() && ctx.camera.prepare(pipe) && pipe.interact("Mend")) {
			options.status = "Repairing pipe";
			options.isSmithing = false;
			Condition.sleep(1000);
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !isBroken(pipe.id());
				}
			}, 700, Random.nextInt(8, 13));
			Condition.sleep(150);
		}
	}
}
