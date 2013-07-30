package org.logicail.api.methods;

import org.powerbot.script.lang.AbstractQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 28/07/13
 * Time: 12:50
 */
public class QueryHelper {
	public static <T extends AbstractQuery<T, K>, K> K first(AbstractQuery<T, K> query) {
		for (K k : query) {
			return k;
		}
		return query.getNil();
	}
}
