package org.logicail.api.methods;

import org.powerbot.script.methods.MethodProvider;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:59
 */
public class MyMethodProvider extends MethodProvider {
	public Logger log = Logger.getLogger(getClass().getSimpleName());

	public MyMethodProvider(MyMethodContext context) {
		super(context);
		ctx = context;
	}
}
