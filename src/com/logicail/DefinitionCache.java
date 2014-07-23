package com.logicail;

import com.logicail.loader.rt4.wrappers.Definition;
import com.logicail.loader.rt4.wrappers.loaders.WrapperLoader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 19:29
 */
public class DefinitionCache<C extends org.powerbot.script.ClientContext, T extends Definition> extends org.powerbot.script.ClientAccessor<C> {
	private static final int MAX_ENTRIES = 100;

	private final WrapperLoader<T> loader;

	private LinkedHashMap<Integer, T> cache = new LinkedHashMap<Integer, T>(MAX_ENTRIES + 1, 0.75f, true) {
		public boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	public DefinitionCache(C ctx, WrapperLoader<T> loader) {
		super(ctx);
		this.loader = loader;
	}

	public T get(int id) {
		if (id < 0) {
			return null;
		}

		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			try {
				T definition = loader.load(id);
				cache.put(id, definition);
				return definition;
			} catch (Exception ignored) {
			}
		}

		// TODO: Consider placing child() here

		return null;
	}
}
