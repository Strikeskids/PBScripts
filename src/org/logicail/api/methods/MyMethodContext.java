package org.logicail.api.methods;

import org.logicail.api.providers.*;
import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:56
 */
public class MyMethodContext extends MethodContext {
	public Interaction interaction;
	public SkillingInterface skillingInterface;
	public MyBackpack backpack;
	public Waiting waiting;
	public MyMouse mouse;
	public MyCamera camera;
	public MyMovement movement;

	public MyMethodContext(MethodContext original) {
		super(original.getBot());
	}

	@Override
	public void init(MethodContext ctx) {
		super.init(ctx);
		interaction = new Interaction(this);
		skillingInterface = new SkillingInterface(this);
		backpack = new MyBackpack(this);
		waiting = new Waiting(this);
		mouse = new MyMouse(this);
		camera = new MyCamera(this);
		movement = new MyMovement(this);
	}
}
