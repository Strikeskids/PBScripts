package org.logicail.cache.loader.rt6.wrapper.loaders;

import org.logicail.cache.loader.rt6.wrapper.ItemDefinition;
import com.sk.cache.fs.Archive;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.CacheType;
import com.sk.cache.fs.FileData;
import com.sk.cache.wrappers.loaders.WrapperLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:17
 */
public class ItemDefinitionLoader extends WrapperLoader<ItemDefinition> {
	protected final CacheType cache;

	public ItemDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem);
		this.cache = cacheSystem.getCacheSource().getCacheTypeRS3(19);
	}

	protected FileData getValidFile(int id) {
		FileData ret = getFile(id);
		if (ret == null)
			throw new IllegalArgumentException("Bad id");
		return ret;
	}

	protected FileData getFile(int id) {
		Archive archive = cache.getArchive(id >>> 8);
		if (archive == null)
			return null;
		return archive.getFile(id & 0xff);
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


