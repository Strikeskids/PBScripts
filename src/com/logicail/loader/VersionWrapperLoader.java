package com.logicail.loader;

import com.logicail.loader.rt4.wrappers.Wrapper;
import com.logicail.loader.rt4.wrappers.loaders.WrapperLoader;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.CacheType;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:28
 */
public abstract class VersionWrapperLoader<T extends Wrapper> extends WrapperLoader<T> {
	public final int version;

	public VersionWrapperLoader(CacheSystem cacheSystem, CacheType cache) {
		super(cacheSystem, cache);
		version = cache.getTable().getVersion();
	}
}
