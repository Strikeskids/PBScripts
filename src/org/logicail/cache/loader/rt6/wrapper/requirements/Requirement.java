package org.logicail.cache.loader.rt6.wrapper.requirements;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 20:42
 */
public interface Requirement<T> {
	public T get(Map<Integer, Object> params);
}
