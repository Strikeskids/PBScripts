package com.logicail.wrappers.loaders;

import com.logicail.wrappers.ClientScriptDefinition;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class ClientScriptDefinitionLoader extends Loader<ClientScriptDefinition> {
	public ClientScriptDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 8);
	}

	public ClientScriptDefinition get(int id) {
		final FileData data = getValidFile(id);
		if (data.getData().length <= 1) {
			throw new IllegalArgumentException("Empty");
		}
		return new ClientScriptDefinition(id, data.getDataAsStream());
	}
}
