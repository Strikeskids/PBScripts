package com.logicail.loader.rt6;

import com.logicail.loader.VersionWrapperLoader;
import com.logicail.loader.rt6.wrapper.ItemDefinition;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:17
 */
public class ItemDefinitionLoader extends VersionWrapperLoader<ItemDefinition> {
	public ItemDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(19));

		ItemDefinition.loader = this;
	}

	@Override
	public ItemDefinition load(int id) {
		final FileData data = getValidFile(id);
		ItemDefinition ret = new ItemDefinition(this, id);
		ret.decode(data.getDataAsStream());
		ret.fix();
		return ret;
	}

	@Override
	public boolean canLoad(int id) {
		return getFile(id) != null;
	}
}
