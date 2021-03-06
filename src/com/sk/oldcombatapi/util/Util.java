package com.sk.oldcombatapi.util;

import org.powerbot.script.AbstractQuery;
import org.powerbot.script.rt6.ClientContext;

import java.util.HashMap;
import java.util.Map;

public class Util {
	public static int indexOf(int[] array, int search) {
		for (int i = 0; i < array.length; ++i)
			if (array[i] == search)
				return i;
		return -1;
	}

	public static <K, V> Map<K, V> toMap(K[] ka, V[] va) {
		Map<K, V> ret = new HashMap<K, V>();
		for (int i = 0; i < ka.length && i < va.length; ++i)
			ret.put(ka[i], va[i]);
		return ret;
	}

	//@SafeVarargs
	public static <K> K[] asArray(K... input) {
		return input;
	}

	public static <T extends AbstractQuery<T, K, ClientContext>, K> K get(AbstractQuery<T, K, ClientContext> input) {
		for (K ret : input)
			return ret;
		return input.nil();
	}
}
