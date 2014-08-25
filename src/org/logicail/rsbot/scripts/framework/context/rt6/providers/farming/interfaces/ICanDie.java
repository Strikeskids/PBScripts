package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.util.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject.FarmingObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/04/2014
 * Time: 10:51
 */
public interface ICanDie {
	/**
	 * Is the patch dead
	 *
	 * @return <tt>true</tt> if the herb has died, otherwise <tt>false</tt>
	 * @see FarmingHelper#dead(FarmingObject)
	 */
	boolean dead();

	/**
	 * Is the patch diseased
	 *
	 * @return <tt>true</tt> if the patch iss diseases, otherwise <tt>false</tt>
	 * @see FarmingHelper#diseased(FarmingObject)
	 */
	boolean diseased();
}
