package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 23:23
 */
public interface IFarmingObjectAccessor<T> {
	T object(IClientContext ctx);
}
