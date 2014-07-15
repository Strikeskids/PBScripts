package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 23:11
 */
public interface IGrowthStage {
	/**
	 * Growth stage
	 *
	 * @return 0 (empty) to X depends on how many stages object has
	 */
	int stage();
}
