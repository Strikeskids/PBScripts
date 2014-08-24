package org.logicail.cache.loader.rt4.wrappers.loaders;

import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;
import org.logicail.cache.loader.rt4.wrappers.ObjectDefinition;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class ObjectDefinitionLoader extends ArchiveLoader<ObjectDefinition> {
	public ObjectDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheTypeOS(2), 6);
	}

	@Override
	public ObjectDefinition load(int id) {
		FileData data = getValidFile(id);
		ObjectDefinition ret = new ObjectDefinition(this, id);
		ret.decode(data.getDataAsStream());
		return ret;
	}
}
