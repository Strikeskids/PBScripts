package org.logicail.cache;

import com.sk.cache.wrappers.Wrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 19:29
 */
public class DefinitionCache<C extends org.powerbot.script.ClientContext, T extends Wrapper> extends org.powerbot.script.ClientAccessor<C> {
	private static final int MAX_ENTRIES = 256;

	private final com.sk.cache.wrappers.loaders.WrapperLoader<T> loader;

	private LinkedHashMap<Integer, T> cache = new LinkedHashMap<Integer, T>(MAX_ENTRIES + 1, 0.75f, true) {
		public boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	public DefinitionCache(C ctx, com.sk.cache.wrappers.loaders.WrapperLoader<T> loader) {
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
