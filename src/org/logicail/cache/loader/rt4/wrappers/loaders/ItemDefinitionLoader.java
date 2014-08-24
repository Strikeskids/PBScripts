package org.logicail.cache.loader.rt4.wrappers.loaders;

import org.logicail.cache.loader.rt4.wrappers.ItemDefinition;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class ItemDefinitionLoader extends ArchiveLoader<ItemDefinition> {
	public ItemDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 10);
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
