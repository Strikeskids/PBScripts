package org.logicail.api.methods;

import org.logicail.api.providers.*;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 11:56
 */
public class LogicailMethodContext extends MethodContext {
	public Interaction interaction;
	public SkillingInterface skillingInterface;
	public LogicailBackpack backpack;
	public Waiting waiting;
	public MyMouse mouse;
	public LogicailCamera camera;
	public MyMovement movement;
	public AbstractScript script;
	public AnimationHistory animationHistory;

	public LogicailMethodContext(AbstractScript script, MethodContext original) {
		super(original.getBot());
		this.script = script;
	}

	// 	/*public static <J extends Job> J getInstance(Class<J> param_class) {

	@Override
	public void init(MethodContext ctx) {
		super.init(ctx);
		interaction = new Interaction(this);
		skillingInterface = new SkillingInterface(this);
		super.backpack = backpack = new LogicailBackpack(this);
		waiting = new Waiting(this);
		super.mouse = mouse = new MyMouse(this);
		camera = new LogicailCamera(this);
		super.movement = movement = new MyMovement(this);
		animationHistory = new AnimationHistory(this);
	}
}
