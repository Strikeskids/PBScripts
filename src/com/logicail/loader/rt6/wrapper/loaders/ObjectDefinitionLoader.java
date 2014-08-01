package com.logicail.loader.rt6.wrapper.loaders;

import com.logicail.loader.rt6.wrapper.ObjectDefinition;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;
import com.sk.cache.wrappers.loaders.ProtocolWrapperLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/2014
 * Time: 17:30
 */
public class ObjectDefinitionLoader extends ProtocolWrapperLoader<ObjectDefinition> {
	public ObjectDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(16));
	}

	@Override
	public ObjectDefinition load(int id) {
		FileData data = getValidFile(id);
		ObjectDefinition ret = new ObjectDefinition(this, id);
		ret.decode(data.getDataAsStream());
		//fixObject(ret);
		return ret;
	}

//	private void fixObject(ObjectDefinition ret) {
//		if (ret.unwalkable) {
//			ret.solid = false;
//			ret.blockType = 0;
//		}
//	}
}
