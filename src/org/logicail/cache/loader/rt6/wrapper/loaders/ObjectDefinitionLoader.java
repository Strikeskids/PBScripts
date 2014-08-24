package org.logicail.cache.loader.rt6.wrapper.loaders;

import org.logicail.cache.loader.rt6.wrapper.ObjectDefinition;
import com.sk.cache.fs.Archive;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.CacheType;
import com.sk.cache.fs.FileData;
import com.sk.cache.wrappers.loaders.WrapperLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/2014
 * Time: 17:30
 */
public class ObjectDefinitionLoader extends WrapperLoader<ObjectDefinition> {
	protected final CacheType cache;

	public ObjectDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem);
		cache = cacheSystem.getCacheSource().getCacheTypeRS3(16);
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
	public ObjectDefinition load(int id) {
		FileData data = getValidFile(id);
		ObjectDefinition ret = new ObjectDefinition(this, id);
		ret.decode(data.getDataAsStream());
		//fixObject(ret);
		return ret;
	}

	@Override
	public boolean canLoad(int id) {
		return false;
	}

//	private void fixObject(ObjectDefinition ret) {
//		if (ret.unwalkable) {
//			ret.solid = false;
//			ret.blockType = 0;
//		}
//	}
}
