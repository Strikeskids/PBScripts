package com.logicail.loader.rt4;

import com.logicail.loader.rt4.wrappers.loaders.ItemDefinitionLoader;
import com.logicail.loader.rt4.wrappers.loaders.NpcDefinitionLoader;
import com.logicail.loader.rt4.wrappers.loaders.ObjectDefinitionLoader;
import com.logicail.loader.rt4.wrappers.loaders.ScriptDefinitionLoader;
import com.sk.cache.fs.CacheSystem;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:24
 */
public class RT4CacheSystem extends CacheSystem {
	public NpcDefinitionLoader npcLoader;
	public ScriptDefinitionLoader varpLoader;
	public ObjectDefinitionLoader objectLoader;
	public ItemDefinitionLoader itemLoader;

	public RT4CacheSystem(File cacheFolder) throws FileNotFoundException {
		super(cacheFolder);

		npcLoader = new NpcDefinitionLoader(this);
		varpLoader = new ScriptDefinitionLoader(this);
		objectLoader = new ObjectDefinitionLoader(this);
		itemLoader = new ItemDefinitionLoader(this);
	}


}
