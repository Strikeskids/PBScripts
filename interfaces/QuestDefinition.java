package org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces;

import org.logicail.rsbot.scripts.framework.context.providers.farming.Config;
import org.powerbot.script.rt6.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 08:38
 */
public class QuestDefinition {
	public final String name;
	private final Config config;
	private final int start;
	private final int end;

	public QuestDefinition(String name, Config config, int start, int end) {
		this.name = name;
		this.config = config;
		this.start = start;
		this.end = end;
	}

	public boolean complete(ClientContext ctx) {
		return ctx.varpbits.varpbit(config.index, config.shift, config.mask) == end;
	}

	public boolean started(ClientContext ctx) {
		return ctx.varpbits.varpbit(config.index, config.shift, config.mask) >= start;
	}
}
