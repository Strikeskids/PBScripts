package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 23:08
 */
public interface ICheckHealth {
	/**
	 * Does the crop have action "Check-health"
	 *
	 * @return if the tree has finished growing and you can check its health
	 * @see FarmingHelper#checkHealth(FarmingObject)
	 */
	boolean checkHealth();
}
