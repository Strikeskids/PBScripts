package com.logicail.loader.rt6.wrapper.requirements;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 20:44
 */
public interface ListRequirement<T> {
	public List<T> get(Map<Integer, Object> params);
}
