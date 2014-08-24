package org.logicail.cache.loader.rt4.wrappers.loaders;

import org.logicail.cache.loader.rt4.wrappers.Script;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class ScriptDefinitionLoader extends ArchiveLoader<Script> {
	public ScriptDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheTypeOS(2), 14);
	}

	@Override
	public Script load(int id) {
		final FileData data = getValidFile(id);
		final Script definition = new Script(this, id);
		definition.decode(data.getDataAsStream());
		return definition;
	}
}
