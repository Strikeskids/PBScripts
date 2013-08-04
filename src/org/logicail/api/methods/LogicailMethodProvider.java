package org.logicail.api.methods;

import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Random;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:59
 */
public class LogicailMethodProvider extends MethodProvider {
	public Logger log = Logger.getLogger(getClass().getSimpleName());
	public LogicailMethodContext ctx;

	public LogicailMethodProvider(LogicailMethodContext context) {
		super(context);
		this.ctx = context;
	}

	@Override
	public void sleep(int min, int max) {
		super.sleep(min, max);
		if (Random.nextBoolean()) {
			super.sleep(0, max - min);
		}
	}
}
