package org.logicail.rsbot.scripts.framework;

import org.powerbot.script.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 21/05/2014
 * Time: 21:10
 */
public class LogicailGraphScript<C extends ClientContext> extends GraphScript<C> {
	private String status = "";

	public String status() {
		return status;
	}

	public void status(String status) {
		if (status != null) {
			log.info(status);
		}
		this.status = status == null ? "" : status;
	}
}
