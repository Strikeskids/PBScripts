package com.logicail.wrappers.loaders;

import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;
import com.logicail.wrappers.ScriptDefinition;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class ScriptDefinitionLoader extends Loader<ScriptDefinition> {
	public ScriptDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 14);
	}

	public ScriptDefinition get(int id) {
		final FileData data = getValidFile(id);

		return new ScriptDefinition(id, data.getDataAsStream());
	}
}
