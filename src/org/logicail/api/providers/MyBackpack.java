package org.logicail.api.providers;

import org.powerbot.script.methods.Backpack;
import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:38
 */
public class MyBackpack extends Backpack {
	public static final int BACKPACK_SIZE = 28;

	public MyBackpack(MethodContext arg0) {
		super(arg0);
	}

	public boolean isFull() {
		return select().count() == BACKPACK_SIZE;
	}
}
