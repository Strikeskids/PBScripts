package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/07/2014
 * Time: 12:51
 */
public class SetRun extends GraphScript.Action<RT4ClientContext> {
	public SetRun(RT4ClientContext ctx) {
		super(ctx);
	}

	@Override
	public String toString() {
		return "Set Run";
	}

	private int nextPercentValid = Random.nextInt(20, 40);

	private long nextrun;

	@Override
	public void run() {
		nextrun = System.currentTimeMillis() + Random.nextInt(10000, 60000);
		if (ctx.movement.running(true)) {
			nextPercentValid = Random.nextInt(20, 40);
		}
		Condition.sleep(200);
	}

	private int energy() {
		final int energy = ctx.movement.energyLevel();
		if (energy > 0) {
			return energy;
		}

		final Component component = ctx.widgets.widget(548).component(95);
		if (component.valid()) {
			try {
				return Integer.parseInt(component.text().trim());
			} catch (NumberFormatException e) {
				log.severe("Run widget wrong");
			}
		}
		return 0;
	}

	@Override
	public boolean valid() {
		return System.currentTimeMillis() > nextrun && !ctx.movement.running() && energy() > nextPercentValid && ctx.varpbits.varpbit(406) > 0;
	}
}
