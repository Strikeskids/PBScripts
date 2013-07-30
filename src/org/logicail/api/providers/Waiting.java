package org.logicail.api.providers;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:59
 */
public class Waiting extends LogicailMethodProvider {
	public Waiting(LogicailMethodContext context) {
		super(context);
	}

	public boolean wait(int min, Condition condition) {
		Timer timer = new Timer(Random.nextInt(min, min * 2));
		while (timer.isRunning() && !condition.validate()) {
			sleep(50, 100);
		}
		return condition.validate();
	}
}
