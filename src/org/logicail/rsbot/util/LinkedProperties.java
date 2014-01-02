package org.logicail.rsbot.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 29/12/13
 * Time: 11:56
 */
public class LinkedProperties extends Properties {
	private static final long serialVersionUID = 4112578634029874841L;
	private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

	@Override
	public Enumeration<Object> keys() {
		return Collections.enumeration(keys);
	}

	@Override
	public Object put(Object key, Object value) {
		keys.add(key);
		return super.put(key, value);
	}

	@Override
	public synchronized Object remove(Object key) {
		keys.remove(key);
		return super.remove(key);
	}
}
