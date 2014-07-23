package com.sk.cache.fs;

import com.sk.cache.DataSource;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class CacheSystem {
	protected final CacheSource cache;

	public CacheSystem(CacheSource cache) {
		this.cache = cache;
	}

	public CacheSystem(DataSource source) {
		this(new CacheSource(source));
	}

	public CacheSystem(File cacheFolder) throws FileNotFoundException {
		this(new DataSource(cacheFolder));
	}

	public CacheSource getCacheSource() {
		return cache;
	}
}
