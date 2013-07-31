package org.logicail.api.methods;

import org.logicail.api.methods.providers.*;
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
	public MyBackpack backpack;
	public Waiting waiting;
	public MyMouse mouse;
	public MyMovement movement;
	public AbstractScript script;
	public AnimationHistory animationHistory;
	public ChatOptions chatOptions;
	public MyWidgets widgets;

	public LogicailMethodContext(MethodContext original, AbstractScript script) {
		super(original.getBot());
		this.script = script;
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
}
