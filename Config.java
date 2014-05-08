package org.logicail.rsbot.scripts.framework.context.providers.farming;

import com.eclipsesource.json.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 08:53
 */
public class Config {
	public final int index;
	public final int shift;
	public final int mask;

	public Config(JsonObject config) {
		index = config.get("index").asInt();
		shift = config.get("shift").asInt();
		mask = config.get("mask").asInt();
	}

	public Config(int index, int shift, int mask) {
		this.index = index;
		this.shift = shift;
		this.mask = mask;
	}
}
