package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.util.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject.FarmingObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 23:06
 */
public interface IStump {
	/**
	 * Is the crop a stump and can therefore be cleared
	 *
	 * @return if the crop is a stump
	 * @see FarmingHelper#stump(FarmingObject)
	 */
	boolean stump();
}
