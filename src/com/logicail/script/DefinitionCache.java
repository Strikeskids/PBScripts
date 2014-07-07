package com.logicail.script;

import com.logicail.wrappers.Definition;
import com.logicail.wrappers.loaders.Loader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 19:29
 */
public class DefinitionCache<T extends Definition> {
	private static final int MAX_ENTRIES = 100;

	private final Loader<T> loader;

	private LinkedHashMap<Integer, T> cache = new LinkedHashMap<Integer, T>(MAX_ENTRIES + 1, 0.75f, true) {
		public boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	public DefinitionCache(Loader<T> loader) {
		this.loader = loader;
	}

	public T get(int id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		}

		try {
			final T t = loader.get(id);
			cache.put(id, loader.get(id));
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
