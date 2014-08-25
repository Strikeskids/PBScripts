package com.sk.cache.wrappers.loaders;

import com.sk.cache.wrappers.region.Region;
import org.logicail.cache.loader.rt6.RT6CacheSystem;

public class RegionLoader extends RegionDataLoader<Region> {

	public RegionLoader(RT6CacheSystem cacheSystem) {
		super(cacheSystem);
	}

	@Override
	public Region load(int regionHash) {
		Region ret = new Region(this, regionHash);
		ret.decode(getData(regionHash, 3));
		ret.addObjects(((RT6CacheSystem)cacheSystem).localObjectLoader.load(regionHash));
		return ret;
	}
}
