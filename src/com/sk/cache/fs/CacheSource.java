package com.sk.cache.fs;

import com.sk.cache.DataSource;

import java.io.IOException;

public class CacheSource {

	private final DataSource source;
	private final CacheType[] types = new CacheType[DataSource.MAX_INDEX_FILES];
	private final IndexFile metaIndex;

	public CacheSource(DataSource source) {
		this.source = source;
		this.metaIndex = new MetaIndexFile(source);
	}

	public IndexFile getMetaIndex() {
		try {
			metaIndex.init();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return metaIndex;
	}

//	public IndexFile getIndex(int cacheType) {
//		return getCacheType(cacheType).getIndex();
//	}

//	public ReferenceTable getReferenceTable(int cacheType) {
//		return getCacheType(cacheType).getTable();
//	}

	public CacheType getCacheTypeRS3(int cacheType) {
		return getCacheType(cacheType, true);
	}

	public CacheType getCacheTypeOS(int cacheType) {
		return getCacheType(cacheType, false);
	}

	private CacheType getCacheType(int cacheType, boolean rs3) {
		if (!source.validateType(cacheType))
			throw new IndexOutOfBoundsException();
		if (types[cacheType] == null) {
			types[cacheType] = new CacheType(this, cacheType, rs3);
			if (!types[cacheType].init())
				throw new RuntimeException("Failed to initialize table and index");
		}
		return types[cacheType];
	}

	public DataSource getSourceSystem() {
		return source;
	}

}
