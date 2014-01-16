package org.logicail.rsbot.scripts.framework;

import org.powerbot.script.AbstractScript;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/01/14
 * Time: 17:42
 */
public abstract class LogicailGui<T extends AbstractScript> extends JFrame {
	protected T script;

	public void setScript(T script) {
		this.script = script;
		setTitle(script.getName() + " v" + script.getVersion());
	}
}
