package org.logicail.cache.loader.rt6.wrapper.loaders;

import com.sk.cache.fs.Archive;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;
import com.sk.cache.wrappers.loaders.WrapperLoader;
import org.logicail.cache.loader.rt6.wrapper.Script;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/08/2014
 * Time: 22:53
 */
public class ScriptLoader extends WrapperLoader<Script> {
	private final Archive source;

	public ScriptLoader(CacheSystem cacheSystem) {
		super(cacheSystem);
		this.source = cacheSystem.getCacheSource().getCacheType(2).getArchive(69);
	}

	@Override
	public Script load(int id) {
		FileData data = source.getFile(id);
		if (data == null)
			throw new IllegalArgumentException("Bad script id");
		Script ret = new Script(this, id);
		ret.decode(data.getDataAsStream());
		return ret;
	}

	@Override
	public boolean canLoad(int id) {
		return source.getFile(id) != null;
	}
}
